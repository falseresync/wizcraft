package falseresync.wizcraft.networking;

import falseresync.wizcraft.networking.c2s.*;
import falseresync.wizcraft.networking.s2c.*;
import net.fabricmc.fabric.api.networking.v1.*;

public class WizcraftNetworking {
    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(TriggerReportS2CPacket.ID, TriggerReportS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TriggerMultiplayerReportS2CPacket.ID, TriggerMultiplayerReportS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TriggerBlockPatternTipS2CPacket.ID, TriggerBlockPatternTipS2CPacket.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(ChangeWandFocusC2SPacket.ID, ChangeWandFocusC2SPacket.PACKET_CODEC);
    }
}