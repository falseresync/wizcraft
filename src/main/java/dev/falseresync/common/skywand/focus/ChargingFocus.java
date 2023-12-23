package dev.falseresync.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.client.gui.hud.WizcraftHud;
import dev.falseresync.common.Wizcraft;
import dev.falseresync.common.item.WizItems;
import dev.falseresync.common.skywand.SkyWand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class ChargingFocus extends Focus {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "charging");
    public static final Codec<ChargingFocus> CODEC = Codec.unit(WizFocuses.CHARGING);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Codec<? extends Focus> getCodec() {
        return CODEC;
    }

    @Override
    public ChargingFocus getType() {
        return WizFocuses.CHARGING;
    }

    @Override
    public ActionResult use(World world, SkyWand wand, LivingEntity user) {
        Wizcraft.LOGGER.trace("Attempting to charge a wand");
        world.calculateAmbientDarkness();
        Integer lightLevel = null;
        if (!world.isNight() || world.isRaining() || (lightLevel = world.getLightLevel(LightType.SKY, user.getBlockPos())) < world.getMaxLightLevel() * 0.5) {
            Wizcraft.LOGGER.trace("It's not night (%s) or it's raining (%s) or it's not lighty (%s)"
                    .formatted(!world.isNight(), world.isRaining(), lightLevel));
            reportCannotCharge(world, user);
            return ActionResult.FAIL;
        }

        var viewDistance = world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.watchDistance;
        HitResult.Type hitType = null;
        if ((hitType = user.raycast(viewDistance * 16, 0, true).getType()) != HitResult.Type.MISS) {
            Wizcraft.LOGGER.trace("View distance (%s) is wack or raycast failed (%s)"
                    .formatted(viewDistance, hitType));
            reportCannotCharge(world, user);
            return ActionResult.FAIL;
        }

        if (wand.isFullyCharged()) {
            reportFullyCharged(world, user);
            return ActionResult.PASS;
        }

        user.setCurrentHand(user.getActiveHand());
        return ActionResult.SUCCESS;
    }

    @Override
    public void finish(World world, SkyWand wand, LivingEntity user) {
        wand.incrementCharge();
        reportSuccessfullyCharged(world, user);
    }

    protected void reportCannotCharge(World world, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("Look up at the surface"));
        }
    }

    protected void reportFullyCharged(World world, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
            WizcraftHud.STATUS_LABEL.setOrReplace(Text.literal("Already fully charged"));
        }
    }

    protected void reportSuccessfullyCharged(World world, LivingEntity user) {
        user.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.5F, 1.25F);
        if (world.isClient()) {
            var pos = user.getPos().add(user.getHandPosOffset(WizItems.SKY_WAND));
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
}
