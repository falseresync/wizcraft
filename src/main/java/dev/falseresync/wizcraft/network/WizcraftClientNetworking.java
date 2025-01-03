package dev.falseresync.wizcraft.network;

import dev.falseresync.wizcraft.client.block.BlockPatternTip;
import dev.falseresync.wizcraft.network.s2c.TriggerBlockPatternTipS2CPacket;
import dev.falseresync.wizcraft.network.s2c.TriggerMultiplayerReportS2CPacket;
import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class WizcraftClientNetworking {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerReportS2CPacket.ID, WizcraftClientNetworking::triggerReport);
        ClientPlayNetworking.registerGlobalReceiver(TriggerMultiplayerReportS2CPacket.ID, WizcraftClientNetworking::triggerMultiplayerReport);
        ClientPlayNetworking.registerGlobalReceiver(TriggerBlockPatternTipS2CPacket.ID, WizcraftClientNetworking::triggerBlockPatternTip);
    }

    private static void triggerReport(TriggerReportS2CPacket packet, ClientPlayNetworking.Context context) {
        packet.report().executeOnClient(context.player());
    }

    private static void triggerMultiplayerReport(TriggerMultiplayerReportS2CPacket packet, ClientPlayNetworking.Context context) {
        packet.report().executeOnNearbyClients(context.player());
    }

    private static void triggerBlockPatternTip(TriggerBlockPatternTipS2CPacket packet, ClientPlayNetworking.Context context) {
        BlockPatternTip.spawnCompletionTipParticles(context.player(), (ClientWorld) context.player().getWorld(), packet.missingBlocks());
    }
}
