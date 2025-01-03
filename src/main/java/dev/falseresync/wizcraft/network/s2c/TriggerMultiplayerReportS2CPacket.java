package dev.falseresync.wizcraft.network.s2c;

import dev.falseresync.wizcraft.api.WizcraftRegistries;
import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public record TriggerMultiplayerReportS2CPacket(MultiplayerReport report) implements CustomPayload {
    public static final CustomPayload.Id<TriggerMultiplayerReportS2CPacket> ID = new Id<>(wid("trigger_multiplayer_report"));
    public static final PacketCodec<RegistryByteBuf, TriggerMultiplayerReportS2CPacket> PACKET_CODEC =
            Identifier.PACKET_CODEC.xmap(
                            id -> new TriggerMultiplayerReportS2CPacket(WizcraftRegistries.REPORTS.getOrEmpty(id)
                                    .filter(it -> it instanceof MultiplayerReport)
                                    .map(it -> (MultiplayerReport) it)
                                    .orElseThrow(() -> new IllegalStateException("Unknown multiplayer report ID: %s".formatted(id)))),
                            packet -> packet.report.getId())
                    .cast();

    @Override
    public Id<TriggerMultiplayerReportS2CPacket> getId() {
        return ID;
    }
}
