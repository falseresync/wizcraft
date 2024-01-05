package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetInstancePriority;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.WizUtils;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWandData;
import dev.falseresync.wizcraft.network.ClientSideReport;
import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChargingFocus extends Focus {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "charging");
    public static final Codec<ChargingFocus> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf("charging_progress", 0).forGetter(ChargingFocus::getChargingProgress)
            ).apply(instance, ChargingFocus::new));
    protected int chargingProgress = 0;

    public ChargingFocus() {
    }

    public ChargingFocus(int chargingProgress) {
        this.chargingProgress = chargingProgress;
    }

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
    public void tick(World world, SkyWandData wand, LivingEntity user, int remainingUseTicks) {
        super.tick(world, wand, user, remainingUseTicks);
        chargingProgress += 1;
    }

    @Override
    public void interrupt(World world, SkyWandData wand, LivingEntity user, int remainingUseTicks) {
        super.interrupt(world, wand, user, remainingUseTicks);
        chargingProgress = 0;
    }

    @Override
    public void finish(World world, SkyWandData wand, LivingEntity user) {
        super.finish(world, wand, user);
        chargingProgress = 0;
        wand.addCharge(40);
        if (user instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, new TriggerReportS2CPacket(ClientSideReport.SUCCESSFULLY_CHARGED));
        }
    }

    public int getChargingProgress() {
        return chargingProgress;
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
}
