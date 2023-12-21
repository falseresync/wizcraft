package dev.falseresync.common.item;

import dev.falseresync.client.gui.hud.WizcraftHud;
import dev.falseresync.common.Wizcraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
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
import net.minecraft.util.math.MathHelper;
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
        var wand = new SkyWand(stack);

        world.calculateAmbientDarkness();
        Integer lightLevel = null;
        if (!world.isNight() || world.isRaining() || (lightLevel = world.getLightLevel(LightType.SKY, user.getBlockPos())) < world.getMaxLightLevel() * 0.5) {
            Wizcraft.LOGGER.trace("It's not night (%s) or it's raining (%s) or it's not lighty (%s)"
                    .formatted(!world.isNight(), world.isRaining(), lightLevel));
            return reportCannotCharge(world, stack, user);
        }

        var viewDistance = world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.watchDistance;
        HitResult.Type hitType = null;
        if ((hitType = user.raycast(viewDistance * 16, 0, true).getType()) != HitResult.Type.MISS) {
            Wizcraft.LOGGER.trace("View distance (%s) is wack or raycast failed (%s)"
                    .formatted(viewDistance, hitType));
            return reportCannotCharge(world, stack, user);
        }

        if (wand.isFullyCharged()) {
            return reportFullyCharged(world, stack, user);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.success(stack);
    }

    protected static TypedActionResult<ItemStack> reportCannotCharge(World world, ItemStack stack, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("Look up at the surface"));
        }
        return TypedActionResult.fail(stack);
    }

    protected static TypedActionResult<ItemStack> reportFullyCharged(World world, ItemStack stack, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("Already fully charged"));
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
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        var wand = new SkyWand(stack);
        var charge = wand.getCharge();
        if (charge >= 10) {
            wand.expendCharge(10);
            wand.saveData();
            var pos = user.getPos().add(user.getHandPosOffset(this));
            var userVelocity = user.getVelocity();
            var yaw = user.getYaw();
            var pitch = user.getPitch();
            var projectile = Util.make(new SnowballEntity(world, pos.getX(), pos.getY(), pos.getZ()), (entity) -> {
                float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
                float g = -MathHelper.sin((pitch + 0) * (float) (Math.PI / 180.0));
                float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
                entity.setVelocity(f, g, h, 3, 1);
                entity.setVelocity(entity.getVelocity()
                        .add(userVelocity.x, user.isOnGround() ? 0.0 : userVelocity.y, userVelocity.z));
            });
            world.spawnEntity(projectile);
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
            }
            WizcraftHud.STATUS_LABEL.setOrReplace(
                    Text.literal("Charged!").styled(style -> style.withColor(Formatting.GOLD)),
                    false);
        }

        user.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.5F, 1.25F);

        var wand = new SkyWand(stack);
        wand.incrementCharge();
        stack = wand.toStack();
        return stack;
    }
}
