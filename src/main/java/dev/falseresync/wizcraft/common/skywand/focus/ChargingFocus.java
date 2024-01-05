package dev.falseresync.wizcraft.common.skywand.focus;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetInstancePriority;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.WizUtils;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWandData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class ChargingFocus extends Focus {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "charging");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public FocusType<ChargingFocus> getType() {
        return WizFocusTypes.CHARGING;
    }

    @Override
    public Item getItem() {
        return WizItems.CHARGING_FOCUS;
    }

    @Override
    public ActionResult use(World world, SkyWandData wand, LivingEntity user) {
        Wizcraft.LOGGER.trace("Attempting to charge a wand");
        world.calculateAmbientDarkness();
        Integer lightLevel = null;
        if (!world.isNight() || world.isRaining() || (lightLevel = world.getLightLevel(LightType.SKY, user.getBlockPos())) < world.getMaxLightLevel() * 0.5) {
            Wizcraft.LOGGER.trace("It's not night (%s) or it's raining (%s) or it's not lighty (%s)"
                    .formatted(!world.isNight(), world.isRaining(), lightLevel));
            reportCannotCharge(world, user);
            return ActionResult.FAIL;
        }

        var viewDistance = WizUtils.findViewDistance(world);
        HitResult.Type hitType = null;
        if ((hitType = user.raycast(viewDistance * 16, 0, true).getType()) != HitResult.Type.MISS) {
            Wizcraft.LOGGER.trace("View distance (%s) is wack or raycast failed (%s)"
                    .formatted(viewDistance, hitType));
            reportCannotCharge(world, user);
            return ActionResult.FAIL;
        }

        if (wand.isFullyCharged()) {
            reportAlreadyCharged(world, user);
            return ActionResult.PASS;
        }

        user.setCurrentHand(user.getActiveHand());
        return ActionResult.SUCCESS;
    }

    @Override
    public void finish(World world, SkyWandData wand, LivingEntity user) {
        wand.addCharge(40);
        reportSuccessfullyCharged(world, user);
    }

    protected void reportCannotCharge(World world, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
            WizHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.sky_wand.cannot_charge"));
        }
    }

    protected void reportAlreadyCharged(World world, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
            WizHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.sky_wand.already_charged"));
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
            WizHud.STATUS_MESSAGE.override(
                    Text.translatable("hud.wizcraft.sky_wand.successfully_charged")
                            .styled(style -> style.withColor(Formatting.GOLD)),
                    WidgetInstancePriority.HIGH);
        }
    }
}
