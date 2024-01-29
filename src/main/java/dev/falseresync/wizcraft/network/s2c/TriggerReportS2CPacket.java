package dev.falseresync.wizcraft.network.s2c;

import dev.falseresync.wizcraft.api.WizcraftRegistries;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.HasId;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record TriggerReportS2CPacket(Report report) implements FabricPacket, HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "trigger_report");
    public static final PacketType<TriggerReportS2CPacket> TYPE = PacketType.create(ID, TriggerReportS2CPacket::new);

    public TriggerReportS2CPacket(PacketByteBuf buf) {
        this(getReportFromId(buf.readIdentifier()));
    }

    private static Report getReportFromId(Identifier id) {
        return WizcraftRegistries.REPORTS.getOrEmpty(id)
                .orElseThrow(() -> new IllegalStateException("Unknown report ID: %s".formatted(id)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(report.getId());
    }

    @Override
    public PacketType<TriggerReportS2CPacket> getType() {
        return TYPE;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
