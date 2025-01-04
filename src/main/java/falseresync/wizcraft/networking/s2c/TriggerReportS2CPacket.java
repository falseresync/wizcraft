package falseresync.wizcraft.networking.s2c;

import falseresync.wizcraft.networking.report.Report;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static falseresync.wizcraft.common.Wizcraft.wid;

public record TriggerReportS2CPacket(Report report) implements CustomPayload {
    public static final CustomPayload.Id<TriggerReportS2CPacket> ID = new Id<>(wid("trigger_report"));
    public static final PacketCodec<RegistryByteBuf, TriggerReportS2CPacket> PACKET_CODEC =
            Identifier.PACKET_CODEC.xmap(
                            id -> new TriggerReportS2CPacket(WizcraftReports.REGISTRY.getOrEmpty(id)
                                    .orElseThrow(() -> new IllegalStateException("Unknown report ID: %s".formatted(id)))),
                            packet -> WizcraftReports.REGISTRY.getId(packet.report())
                    )
                    .cast();

    @Override
    public Id<TriggerReportS2CPacket> getId() {
        return ID;
    }
}