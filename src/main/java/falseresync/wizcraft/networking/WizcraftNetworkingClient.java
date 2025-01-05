package falseresync.wizcraft.networking;

import falseresync.wizcraft.client.BlockPatternTip;
import falseresync.wizcraft.networking.s2c.TriggerBlockPatternTipS2CPacket;
import falseresync.wizcraft.networking.s2c.TriggerMultiplayerReportS2CPacket;
import falseresync.wizcraft.networking.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;

public class WizcraftNetworkingClient {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerReportS2CPacket.ID, WizcraftNetworkingClient::triggerReport);
        ClientPlayNetworking.registerGlobalReceiver(TriggerMultiplayerReportS2CPacket.ID, WizcraftNetworkingClient::triggerMultiplayerReport);
        ClientPlayNetworking.registerGlobalReceiver(TriggerBlockPatternTipS2CPacket.ID, WizcraftNetworkingClient::triggerBlockPatternTip);
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
