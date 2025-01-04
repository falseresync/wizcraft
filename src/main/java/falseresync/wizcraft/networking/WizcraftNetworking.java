package falseresync.wizcraft.networking;

import falseresync.wizcraft.networking.s2c.TriggerMultiplayerReportS2CPacket;
import falseresync.wizcraft.networking.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class WizcraftNetworking {
    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(TriggerReportS2CPacket.ID, TriggerReportS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TriggerMultiplayerReportS2CPacket.ID, TriggerMultiplayerReportS2CPacket.PACKET_CODEC);
    }
}