package dev.falseresync.wizcraft.network;

import dev.falseresync.wizcraft.network.s2c.TriggerMultiplayerReportS2CPacket;
import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;

public class WizClientNetworking {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerReportS2CPacket.TYPE, WizClientNetworking::triggerReport);
        ClientPlayNetworking.registerGlobalReceiver(TriggerMultiplayerReportS2CPacket.TYPE, WizClientNetworking::triggerMultiplayerReport);
    }

    private static void triggerReport(TriggerReportS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        packet.report().executeOnClient(player);
    }

    private static void triggerMultiplayerReport(TriggerMultiplayerReportS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        packet.report().executeOnNearbyClients(player);
    }
}
