package falseresync.wizcraft.networking.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

import static falseresync.wizcraft.common.Wizcraft.wid;

public record TriggerBlockPatternTipS2CPayload(List<BlockPos> missingBlocks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerBlockPatternTipS2CPayload> ID = new Type<>(wid("trigger_block_pattern_tip"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerBlockPatternTipS2CPayload> PACKET_CODEC =
            ByteBufCodecs.collection(n -> (List<BlockPos>) new ArrayList<BlockPos>(n), BlockPos.STREAM_CODEC)
                    .map(TriggerBlockPatternTipS2CPayload::new, TriggerBlockPatternTipS2CPayload::missingBlocks)
                    .cast();

    @Override
    public Type<TriggerBlockPatternTipS2CPayload> type() {
        return ID;
    }
}
