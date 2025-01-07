package falseresync.wizcraft.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import static falseresync.wizcraft.common.Wizcraft.wid;

public record ChangeWandFocusC2SPacket(int slot) implements CustomPayload {
    public static final CustomPayload.Id<ChangeWandFocusC2SPacket> ID = new Id<>(wid("change_wand_focus"));
    public static final PacketCodec<RegistryByteBuf, ChangeWandFocusC2SPacket> PACKET_CODEC =
            PacketCodecs.INTEGER.xmap(ChangeWandFocusC2SPacket::new, ChangeWandFocusC2SPacket::slot).cast();

    @Override
    public Id<ChangeWandFocusC2SPacket> getId() {
        return ID;
    }
}
