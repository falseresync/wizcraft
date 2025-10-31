package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.WizcraftUtil;
import falseresync.wizcraft.common.data.ItemBarComponent;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import falseresync.wizcraft.common.world.WizcraftWorld;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class EnergyVeilFocusItem extends FocusItem {
    public static final int USE_DURATION = 200;
    public static final int STARTING_COST = 10;
    public static final int CONTINUOUS_COST = 2;
    private static final int CYAN_ARGB = FastColor.ARGB32.color(0, 115, 190, 211);
    private static final Color CYAN = Color.ofArgb(CYAN_ARGB);
    private static final Color RED = Color.ofHsv(2 / 360F, 1F, 0.8F);

    public EnergyVeilFocusItem(Properties settings) {
        super(settings);
    }

    @Override
    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, Player user) {
        removeOrphanedVeilReference(wandStack, user);
    }

    @Override
    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, Player user) {
        resetWand(wandStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, Level level, Player user, InteractionHand hand) {
        removeOrphanedVeilReference(wandStack, user);
        if (user instanceof ServerPlayer player
                && !wandStack.has(WizcraftComponents.ENERGY_VEIL_UUID)
                && !user.hasAttached(WizcraftAttachments.ENERGY_VEIL_NETWORK_ID)) {
            if (Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, STARTING_COST, user)) {
                var veil = new EnergyVeilEntity(user, wandStack, level);
                veil.setRadius(2);
                level.addFreshEntity(veil);
                wandStack.set(WizcraftComponents.ENERGY_VEIL_UUID, veil.getUUID());
                wandStack.set(WizcraftComponents.IN_USE, true);
                user.startUsingItem(user.getUsedItemHand());
                return InteractionResultHolder.success(wandStack);
            }

            Reports.insufficientCharge(player);
            return InteractionResultHolder.fail(wandStack);
        }
        return InteractionResultHolder.pass(wandStack);
    }

    @Override
    public void focusOnUseTick(Level level, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
        findVeil(wandStack, level).ifPresent(veil -> {
            if (user instanceof ServerPlayer player) {
                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, CONTINUOUS_COST, player)) {
                    player.hurt(level.damageSources().magic(), 0.1f);
                    var previousDeficit = wandStack.update(WizcraftComponents.CHARGE_DEFICIT, 0, it -> it + CONTINUOUS_COST);
                    if (previousDeficit != null && previousDeficit % (CONTINUOUS_COST * 35) == 0) {
                        EntityType.VEX.spawn((ServerLevel) level, WizcraftUtil.fuzzyPos(level.random, 1, player.blockPosition()), MobSpawnType.TRIGGERED);
                    }
                    if (level.random.nextFloat() < 0.1f) {
                        focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
                    }
                }
                veil.incrementLifeExpectancy(2);
                var maxUseTicks = focusGetUseDuration(wandStack, focusStack, user);
                wandStack.set(
                        WizcraftComponents.ITEM_BAR,
                        new ItemBarComponent(Math.clamp(Math.round((maxUseTicks - remainingUseTicks) * 13f / maxUseTicks), 0, 13), CYAN_ARGB));

                if (level.random.nextFloat() < 0.01f) {
                    focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
                }
            }
        });
    }

    @Override
    public void focusReleaseUsing(ItemStack wandStack, ItemStack focusStack, Level world, LivingEntity user, int remainingUseTicks) {
        focusFinishUsingItem(wandStack, focusStack, world, user);
    }

    @Override
    public ItemStack focusFinishUsingItem(ItemStack wandStack, ItemStack focusStack, Level level, LivingEntity user) {
        if (!level.isClientSide) {
            Optional.ofNullable(wandStack.get(WizcraftComponents.CHARGE_DEFICIT)).ifPresent(deficit -> {
                if (deficit > CONTINUOUS_COST * 50) {
                    // TODO: different sound
                    level.explode(
                            user, level.damageSources().magic(), WizcraftWorld.MagicDischargeExplosionBehavior.INSTANCE,
                            user.getX(), user.getY(), user.getZ(), 3f, false, Level.ExplosionInteraction.TRIGGER,
                            ParticleTypes.ELECTRIC_SPARK, ParticleTypes.EXPLOSION_EMITTER, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(WizcraftSounds.STAR_PROJECTILE_EXPLODE));
                }
            });
            resetWand(wandStack);
            focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
        }
        return wandStack;
    }

    @Override
    public int focusGetUseDuration(ItemStack wandStack, ItemStack focusStack, LivingEntity user) {
        return USE_DURATION;
    }

    @Override
    public void focusInventoryTick(ItemStack wandStack, ItemStack focusStack, Level level, Entity entity, int slot, boolean selected) {
        if (wandStack.has(WizcraftComponents.IN_USE)) return;

        findVeil(wandStack, level).ifPresent(veil -> {
            float delta = Math.clamp(2f * (float) (veil.getLifeExpectancy() - veil.tickCount) / veil.getLifeExpectancy(), 0, 1);
            if (delta <= 1 / 13f) {
                wandStack.remove(WizcraftComponents.ITEM_BAR);
            } else {
                wandStack.set(WizcraftComponents.ITEM_BAR, new ItemBarComponent(Math.clamp(Math.round(delta * 13f), 0, 13), RED.interpolate(CYAN, delta).argb()));
            }
        });
    }

    private void resetWand(ItemStack wandStack) {
        wandStack.remove(WizcraftComponents.IN_USE);
        wandStack.remove(WizcraftComponents.ITEM_BAR);
        wandStack.remove(WizcraftComponents.CHARGE_DEFICIT);
    }

    private Optional<EnergyVeilEntity> findVeil(ItemStack wandStack, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return Optional.ofNullable(wandStack.get(WizcraftComponents.ENERGY_VEIL_UUID)).flatMap(uuid -> {
                if (serverLevel.getEntity(uuid) instanceof EnergyVeilEntity veil) {
                    return Optional.of(veil);
                }

                return Optional.empty();
            });
        }

        return Optional.empty();
    }

    // This has to happen on the server, and only when the component is present, and only in that order
    private void removeOrphanedVeilReference(ItemStack wandStack, Player user) {
        if (user.level() instanceof ServerLevel serverLevel) {
            Optional.ofNullable(wandStack.get(WizcraftComponents.ENERGY_VEIL_UUID)).ifPresent(uuid -> {
                if (!(serverLevel.getEntity(uuid) instanceof EnergyVeilEntity)) {
                    wandStack.remove(WizcraftComponents.ENERGY_VEIL_UUID);
                    user.removeAttached(WizcraftAttachments.ENERGY_VEIL_NETWORK_ID);
                }
            });
        }
    }
}
