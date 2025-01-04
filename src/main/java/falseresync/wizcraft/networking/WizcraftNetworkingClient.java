package falseresync.wizcraft.networking;

import falseresync.wizcraft.networking.s2c.TriggerMultiplayerReportS2CPacket;
import falseresync.wizcraft.networking.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class WizcraftNetworkingClient {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerReportS2CPacket.ID, WizcraftNetworkingClient::triggerReport);
        ClientPlayNetworking.registerGlobalReceiver(TriggerMultiplayerReportS2CPacket.ID, WizcraftNetworkingClient::triggerMultiplayerReport);
    }

    private static void triggerReport(TriggerReportS2CPacket packet, ClientPlayNetworking.Context context) {
        packet.report().executeOnClient(context.player());
    }

    private static void triggerMultiplayerReport(TriggerMultiplayerReportS2CPacket packet, ClientPlayNetworking.Context context) {
        packet.report().executeOnNearbyClients(context.player());
    }
}
