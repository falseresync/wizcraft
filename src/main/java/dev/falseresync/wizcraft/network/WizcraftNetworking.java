package dev.falseresync.wizcraft.network;

import dev.falseresync.wizcraft.network.c2s.UpdateWandFocusC2SPacket;
import dev.falseresync.wizcraft.network.s2c.TriggerBlockPatternTipS2CPacket;
import dev.falseresync.wizcraft.network.s2c.TriggerMultiplayerReportS2CPacket;
import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class WizcraftNetworking {
    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(UpdateWandFocusC2SPacket.ID, UpdateWandFocusC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(TriggerBlockPatternTipS2CPacket.ID, TriggerBlockPatternTipS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TriggerMultiplayerReportS2CPacket.ID, TriggerMultiplayerReportS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TriggerReportS2CPacket.ID, TriggerReportS2CPacket.PACKET_CODEC);
    }
}
