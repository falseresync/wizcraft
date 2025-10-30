package falseresync.wizcraft.networking.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static falseresync.wizcraft.common.Wizcraft.wid;


public record ChangeWandFocusC2SPayload(WandFocusDestination destination, int slot) implements CustomPacketPayload {
    public static final Type<ChangeWandFocusC2SPayload> ID = new Type<>(wid("change_wand_focus"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeWandFocusC2SPayload> PACKET_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT.map(it -> WandFocusDestination.values[it], WandFocusDestination::ordinal).cast(), ChangeWandFocusC2SPayload::destination,
                    ByteBufCodecs.INT, ChangeWandFocusC2SPayload::slot,
                    ChangeWandFocusC2SPayload::new
            );

    @Override
    public Type<ChangeWandFocusC2SPayload> type() {
        return ID;
    }
}
