package falseresync.wizcraft.networking.s2c;

import falseresync.wizcraft.networking.report.MultiplayerReport;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static falseresync.wizcraft.common.Wizcraft.wid;

public record TriggerMultiplayerReportS2CPacket(MultiplayerReport report) implements CustomPayload {
    public static final CustomPayload.Id<TriggerMultiplayerReportS2CPacket> ID = new Id<>(wid("trigger_multiplayer_report"));
    public static final PacketCodec<RegistryByteBuf, TriggerMultiplayerReportS2CPacket> PACKET_CODEC =
            Identifier.PACKET_CODEC.xmap(
                            id -> new TriggerMultiplayerReportS2CPacket(WizcraftReports.REGISTRY.getOrEmpty(id)
                                    .filter(it -> it instanceof MultiplayerReport)
                                    .map(it -> (MultiplayerReport) it)
                                    .orElseThrow(() -> new IllegalStateException("Unknown multiplayer report ID: %s".formatted(id)))),
                            packet -> WizcraftReports.REGISTRY.getId(packet.report()))
                    .cast();

    @Override
    public Id<TriggerMultiplayerReportS2CPacket> getId() {
        return ID;
    }
}