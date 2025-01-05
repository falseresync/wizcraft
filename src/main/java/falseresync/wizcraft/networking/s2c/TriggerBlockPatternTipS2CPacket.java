package falseresync.wizcraft.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static falseresync.wizcraft.common.Wizcraft.wid;

public record TriggerBlockPatternTipS2CPacket(List<BlockPos> missingBlocks) implements CustomPayload {
    public static final CustomPayload.Id<TriggerBlockPatternTipS2CPacket> ID = new Id<>(wid("trigger_block_pattern_tip"));
    public static final PacketCodec<RegistryByteBuf, TriggerBlockPatternTipS2CPacket> PACKET_CODEC =
            PacketCodecs.collection(n -> (List<BlockPos>) new ArrayList<BlockPos>(n), BlockPos.PACKET_CODEC)
                    .xmap(TriggerBlockPatternTipS2CPacket::new, TriggerBlockPatternTipS2CPacket::missingBlocks)
                    .cast();

    @Override
    public Id<TriggerBlockPatternTipS2CPacket> getId() {
        return ID;
    }
}
