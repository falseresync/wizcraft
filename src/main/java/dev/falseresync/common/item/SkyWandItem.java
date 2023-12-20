package dev.falseresync.common.item;

import dev.falseresync.client.gui.hud.WizcraftHud;
import dev.falseresync.common.Wizcraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SkyWandItem extends Item {
    public SkyWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Wizcraft.LOGGER.trace(user.getName() + " started using a wand");
        var stack = user.getStackInHand(hand);

        world.calculateAmbientDarkness();
        if (!world.isNight() || world.isRaining() || world.getLightLevel(LightType.SKY, user.getBlockPos()) < world.getMaxLightLevel() * 0.5) {
            Wizcraft.LOGGER.trace("It's not night (%s) or it's raining (%s) or it's not lighty (%s)"
                    .formatted(!world.isNight(), world.isRaining(), world.getLightLevel(LightType.SKY, user.getBlockPos())));
            return fail(world, stack, user);
        }

        var viewDistance = world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.watchDistance;
        HitResult.Type hitType = null;
        if ((hitType = user.raycast(viewDistance * 16, 0, true).getType()) != HitResult.Type.MISS) {
            Wizcraft.LOGGER.trace("View distance (%s) is wack or raycast failed (%s)"
                    .formatted(viewDistance, hitType));
            return fail(world, stack, user);
        }

        var wand = new SkyWand(stack);
        if (wand.isFullyCharged()) {
            return pass(world, stack, user);
        }

        // TODO(Charging mechanics):
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    protected static TypedActionResult<ItemStack> fail(World world, ItemStack stack, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("test"));
        }
        return TypedActionResult.fail(stack);
    }

    protected static TypedActionResult<ItemStack> pass(World world, ItemStack stack, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("test other"));
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 50;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks % 10 != 0) {
            return;
        }

        if (world.isClient()) {
            user.swingHand(user.getActiveHand());
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " finished using a wand");
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
                user.playSoundIfNotSilent(SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE);
            }

            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("Charged!"), false);
        }

        var wand = new SkyWand(stack);
        wand.incrementCharge();
        stack = wand.toStack();
        return stack;
    }
}
