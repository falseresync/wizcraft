package dev.falseresync.wizcraft.network;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetInstancePriority;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class WizClientNetworking {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerReportS2CPacket.TYPE, WizClientNetworking::triggerReport);
    }

    private static void triggerReport(TriggerReportS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        switch (packet.report()) {
            case INVALID_PEDESTAL_FORMATION -> reportInvalidPedestalFormation(player);
            case SUCCESSFULLY_CHARGED -> reportSuccessfullyCharged(player);
        }
    }

    private static void reportInvalidPedestalFormation(PlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        WizHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.sky_wand.invalid_pedestal_formation"));
    }

    private static void reportSuccessfullyCharged(PlayerEntity user) {
        var world = user.getWorld();
        var rotation = user.getRotationVec(1);
        var orthogonalDistance = 1;
        var pos = user.getEyePos()
                .add(user.getHandPosOffset(WizItems.SKY_WAND))
                .add(rotation.x * orthogonalDistance, -0.25, rotation.z * orthogonalDistance);
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
        user.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, 1F, 1.25F);
        WizHud.STATUS_MESSAGE.override(
                Text.translatable("hud.wizcraft.sky_wand.successfully_charged").styled(style -> style.withColor(Formatting.GOLD)),
                WidgetInstancePriority.HIGH);
    }
}
