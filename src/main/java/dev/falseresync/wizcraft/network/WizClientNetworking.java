package dev.falseresync.wizcraft.network;

import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class WizClientNetworking {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerReportS2CPacket.TYPE, WizClientNetworking::triggerReport);
    }

    public static void triggerReport(TriggerReportS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        switch (packet.report()) {
            case LENSED_WORKTABLE__NOT_ENOUGH_PEDESTALS -> reportNotEnoughPedestals(player);
        }
    }

    public static void reportNotEnoughPedestals(PlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        WizHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.sky_wand.not_enough_pedestals"));
    }
}
