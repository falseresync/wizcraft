package falseresync.wizcraft.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import static falseresync.wizcraft.common.Wizcraft.wid;


public record ChangeWandFocusC2SPayload(WandFocusDestination destination, int slot) implements CustomPayload {
    public static final Id<ChangeWandFocusC2SPayload> ID = new Id<>(wid("change_wand_focus"));
    public static final PacketCodec<RegistryByteBuf, ChangeWandFocusC2SPayload> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER.xmap(it -> WandFocusDestination.values[it], WandFocusDestination::ordinal).cast(), ChangeWandFocusC2SPayload::destination,
                    PacketCodecs.INTEGER, ChangeWandFocusC2SPayload::slot,
                    ChangeWandFocusC2SPayload::new
            );

    @Override
    public Id<ChangeWandFocusC2SPayload> getId() {
        return ID;
    }
}
