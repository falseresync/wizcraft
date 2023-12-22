package dev.falseresync.common.item;

import dev.falseresync.client.gui.hud.WizcraftHud;
import dev.falseresync.common.Wizcraft;
import dev.falseresync.common.entity.StarProjectileEntity;
import dev.falseresync.common.skywand.SkyWand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SkyWandItem extends Item {
    public SkyWandItem(Settings settings) {
        super(settings);
    }

    protected void reportCannotCharge(World world, ItemStack stack, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("Look up at the surface"));
        }
    }

    protected void reportFullyCharged(World world, ItemStack stack, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("Already fully charged"));
        }
    }

    protected void reportSuccessfullyCharged(World world, ItemStack stack, LivingEntity user) {
        user.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.5F, 1.25F);
        if (world.isClient()) {
            var pos = user.getPos().add(user.getHandPosOffset(this));
            var random = world.getRandom();
            for (int i = 0; i < random.nextBetween(5, 10); i++) {
                world.addParticle(
                        ParticleTypes.FIREWORK,
                        pos.x,
                        pos.y,
                        pos.z,
                        (random.nextFloat() - 0.5) / 2,
                        random.nextFloat() / 2,
                        (random.nextFloat() - 0.5) / 2);
            }
            WizcraftHud.STATUS_LABEL.setOrReplace(
                    Text.literal("Charged!").styled(style -> style.withColor(Formatting.GOLD)),
                    false);
        }
    }

    protected TypedActionResult<ItemStack> attemptCharge(World world, ItemStack stack, SkyWand wand, LivingEntity user, Hand hand) {
        Wizcraft.LOGGER.trace("Attempting to charge a wand");
        world.calculateAmbientDarkness();
        Integer lightLevel = null;
        if (!world.isNight() || world.isRaining() || (lightLevel = world.getLightLevel(LightType.SKY, user.getBlockPos())) < world.getMaxLightLevel() * 0.5) {
            Wizcraft.LOGGER.trace("It's not night (%s) or it's raining (%s) or it's not lighty (%s)"
                    .formatted(!world.isNight(), world.isRaining(), lightLevel));
            reportCannotCharge(world, stack, user);
            return TypedActionResult.fail(stack);
        }

        var viewDistance = world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.watchDistance;
        HitResult.Type hitType = null;
        if ((hitType = user.raycast(viewDistance * 16, 0, true).getType()) != HitResult.Type.MISS) {
            Wizcraft.LOGGER.trace("View distance (%s) is wack or raycast failed (%s)"
                    .formatted(viewDistance, hitType));
            reportCannotCharge(world, stack, user);
            return TypedActionResult.fail(stack);
        }

        if (wand.isFullyCharged()) {
            reportFullyCharged(world, stack, user);
            return TypedActionResult.pass(stack);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.success(stack);
    }

    protected TypedActionResult<ItemStack> attemptFire(World world, ItemStack stack, SkyWand wand, LivingEntity user, Hand hand) {

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Wizcraft.LOGGER.trace(user.getName() + " started using a wand");
        var stack = user.getStackInHand(hand);
        var wand = new SkyWand(stack);
        if (wand.isChargingFocusActive()) {
            return attemptCharge(world, stack, wand, user, hand);
        } else {
            return attemptFire(world, stack, wand, user, hand);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 50;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        var wand = new SkyWand(stack);
        attemptFire(world, stack, wand, user, user.getActiveHand());
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " finished using a wand");
        var wand = new SkyWand(stack);
        if (wand.isChargingFocusActive()) {
            wand.incrementCharge();
            stack = wand.toStack();
            reportSuccessfullyCharged(world, stack, user);
        }
        return stack;
    }
}
