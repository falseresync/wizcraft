package dev.falseresync.wizcraft.network.s2c;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record TriggerBlockPatternTipS2CPacket(List<BlockPos> missingBlocks) implements FabricPacket, HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MOD_ID, "trigger_block_pattern_tip");
    public static final PacketType<TriggerBlockPatternTipS2CPacket> TYPE = PacketType.create(ID, TriggerBlockPatternTipS2CPacket::new);

    public TriggerBlockPatternTipS2CPacket(PacketByteBuf buf) {
        this(buf.readList(PacketByteBuf::readBlockPos));
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(missingBlocks, PacketByteBuf::writeBlockPos);
    }

    @Override
    public PacketType<TriggerBlockPatternTipS2CPacket> getType() {
        return TYPE;
    }
}
