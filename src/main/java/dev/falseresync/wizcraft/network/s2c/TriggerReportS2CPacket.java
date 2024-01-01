package dev.falseresync.wizcraft.network.s2c;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.lib.HasId;
import dev.falseresync.wizcraft.network.ClientSideReport;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record TriggerReportS2CPacket(ClientSideReport report) implements FabricPacket, HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "trigger_report");
    public static final PacketType<TriggerReportS2CPacket> TYPE = PacketType.create(ID, TriggerReportS2CPacket::new);

    public TriggerReportS2CPacket(PacketByteBuf buf) {
        this(buf.readEnumConstant(ClientSideReport.class));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(report);
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
