package dev.falseresync.wizcraft.network.s2c;

import dev.falseresync.wizcraft.api.WizcraftRegistries;
import dev.falseresync.wizcraft.api.common.report.Report;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public record TriggerReportS2CPacket(Report report) implements CustomPayload {
    public static final CustomPayload.Id<TriggerReportS2CPacket> ID = new Id<>(wid("trigger_report"));
    public static final PacketCodec<RegistryByteBuf, TriggerReportS2CPacket> PACKET_CODEC =
            Identifier.PACKET_CODEC.xmap(
                        id -> new TriggerReportS2CPacket(WizcraftRegistries.REPORTS.getOrEmpty(id)
                                .orElseThrow(() -> new IllegalStateException("Unknown report ID: %s".formatted(id)))),
                        packet -> packet.report().getId()
                    )
                    .cast();

    @Override
    public Id<TriggerReportS2CPacket> getId() {
        return ID;
    }
}
