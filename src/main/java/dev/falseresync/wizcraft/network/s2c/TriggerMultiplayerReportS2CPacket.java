package dev.falseresync.wizcraft.network.s2c;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.api.WizRegistries;
import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record TriggerMultiplayerReportS2CPacket(MultiplayerReport report) implements FabricPacket, HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "trigger_multiplayer_report");
    public static final PacketType<TriggerMultiplayerReportS2CPacket> TYPE = PacketType.create(ID, TriggerMultiplayerReportS2CPacket::new);

    public TriggerMultiplayerReportS2CPacket(PacketByteBuf buf) {
        this(getReportFromId(buf.readIdentifier()));
    }

    private static MultiplayerReport getReportFromId(Identifier id) {
        return WizRegistries.REPORTS.getOrEmpty(id)
                .filter(it -> it instanceof MultiplayerReport)
                .map(it -> (MultiplayerReport) it)
                .orElseThrow(() -> new IllegalStateException("Unknown multiplayer report ID: %s".formatted(id)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(report.getId());
    }

    @Override
    public PacketType<TriggerMultiplayerReportS2CPacket> getType() {
        return TYPE;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
