package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.data.component.ItemBarComponent;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import falseresync.wizcraft.common.world.WizcraftWorld;
import falseresync.wizcraft.networking.report.WizcraftReports;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public class EnergyVeilFocusItem extends FocusItem {
    private static final int CYAN_ARGB = ColorHelper.Argb.getArgb(0, 115, 190, 211);
    private static final Color CYAN = Color.ofArgb(CYAN_ARGB);
    private static final Color RED = Color.ofHsv(2 / 360F, 1F, 0.8F);
    public static final int MAX_USE_TIME = 200;
    public static final int STARTING_COST = 10;
    public static final int CONTINUOUS_COST = 2;

    public EnergyVeilFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        removeOrphanedVeilReference(wandStack, user);
    }

    @Override
    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        resetWand(wandStack);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        removeOrphanedVeilReference(wandStack, user);
        if (user instanceof ServerPlayerEntity player
                && !wandStack.contains(WizcraftDataComponents.ENERGY_VEIL_UUID)
                && !user.hasAttached(WizcraftDataAttachments.ENERGY_VEIL_NETWORK_ID)) {
            if (Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, STARTING_COST, user)) {
                var veil = new EnergyVeilEntity(user, wandStack, world);
                veil.setVeilRadius(2);
                world.spawnEntity(veil);
                wandStack.set(WizcraftDataComponents.ENERGY_VEIL_UUID, veil.getUuid());
                wandStack.set(WizcraftDataComponents.IN_USE, true);
                user.setCurrentHand(user.getActiveHand());
                return TypedActionResult.success(wandStack);
            }

            WizcraftReports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
            return TypedActionResult.fail(wandStack);
        }
        return TypedActionResult.pass(wandStack);
    }

    @Override
    public void focusUsageTick(World world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
        findVeil(wandStack, world).ifPresent(veil -> {
            if (user instanceof ServerPlayerEntity player) {
                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, CONTINUOUS_COST, player)) {
                    player.damage(world.getDamageSources().magic(), 0.1f);
                    var previousDeficit = wandStack.apply(WizcraftDataComponents.CHARGE_DEFICIT, 0, it -> it + CONTINUOUS_COST);
                    if (previousDeficit != null && previousDeficit % (CONTINUOUS_COST * 35) == 0) {
                        EntityType.VEX.spawn((ServerWorld) world, fuzzyPos(world.random, 1, player.getBlockPos()), SpawnReason.TRIGGERED);
                    }
                }
                veil.incrementLifeExpectancy(2);
                var maxUseTicks = focusGetMaxUseTime(wandStack, focusStack, user);
                wandStack.set(WizcraftDataComponents.ITEM_BAR,
                        new ItemBarComponent(Math.clamp(Math.round((maxUseTicks - remainingUseTicks) * 13f / maxUseTicks), 0, 13), CYAN_ARGB));
            }
        });
    }

    @Override
    public void focusOnStoppedUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
        focusFinishUsing(wandStack, focusStack, world, user);
    }

    @Override
    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user) {
        if (!world.isClient) {
            Optional.ofNullable(wandStack.get(WizcraftDataComponents.CHARGE_DEFICIT)).ifPresent(deficit -> {
                if (deficit > CONTINUOUS_COST * 50) {
                    // TODO: different sound
                    world.createExplosion(
                            user, world.getDamageSources().magic(), WizcraftWorld.MagicDischargeExplosionBehavior.INSTANCE,
                            user.getX(), user.getY(), user.getZ(), 3f, false, World.ExplosionSourceType.TRIGGER,
                            ParticleTypes.ELECTRIC_SPARK, ParticleTypes.EXPLOSION_EMITTER, Registries.SOUND_EVENT.getEntry(WizcraftSounds.STAR_PROJECTILE_EXPLODE));
                }
            });
            resetWand(wandStack);
        }
        return wandStack;
    }

    @Override
    public int focusGetMaxUseTime(ItemStack wandStack, ItemStack focusStack, LivingEntity user) {
        return MAX_USE_TIME;
    }

    @Override
    public void focusInventoryTick(ItemStack wandStack, ItemStack focusStack, World world, Entity entity, int slot, boolean selected) {
        if (wandStack.contains(WizcraftDataComponents.IN_USE)) return;

        findVeil(wandStack, world).ifPresent(veil -> {
            float delta = Math.clamp(2f * (float) (veil.getLifeExpectancy() - veil.age) / veil.getLifeExpectancy(), 0, 1);
            if (delta <= 1/13f) {
                wandStack.remove(WizcraftDataComponents.ITEM_BAR);
            } else {
                wandStack.set(WizcraftDataComponents.ITEM_BAR, new ItemBarComponent(Math.clamp(Math.round(delta * 13f), 0, 13), RED.interpolate(CYAN, delta).argb()));
            }
        });
    }

    private void resetWand(ItemStack wandStack) {
        wandStack.remove(WizcraftDataComponents.IN_USE);
        wandStack.remove(WizcraftDataComponents.ITEM_BAR);
        wandStack.remove(WizcraftDataComponents.CHARGE_DEFICIT);
    }

    private Optional<EnergyVeilEntity> findVeil(ItemStack wandStack, World world) {
        if (world instanceof ServerWorld serverWorld) {
            return Optional.ofNullable(wandStack.get(WizcraftDataComponents.ENERGY_VEIL_UUID)).flatMap(uuid -> {
                if (serverWorld.getEntity(uuid) instanceof EnergyVeilEntity veil) {
                    return Optional.of(veil);
                }

                return Optional.empty();
            });
        }

        return Optional.empty();
    }

    // This has to happen on the server, and only when the component is present, and only in that order
    private void removeOrphanedVeilReference(ItemStack wandStack, PlayerEntity user) {
        if (user.getWorld() instanceof ServerWorld serverWorld) {
            Optional.ofNullable(wandStack.get(WizcraftDataComponents.ENERGY_VEIL_UUID)).ifPresent(uuid -> {
                if (!(serverWorld.getEntity(uuid) instanceof EnergyVeilEntity)) {
                    wandStack.remove(WizcraftDataComponents.ENERGY_VEIL_UUID);
                    user.removeAttached(WizcraftDataAttachments.ENERGY_VEIL_NETWORK_ID);
                }
            });
        }
    }

    private static BlockPos fuzzyPos(Random random, int radius, BlockPos pos) {
        return pos.add(random.nextInt(radius) - radius, 0, random.nextInt(radius) - radius);
    }
}
