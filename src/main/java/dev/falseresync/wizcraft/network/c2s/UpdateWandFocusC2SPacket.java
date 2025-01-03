package dev.falseresync.wizcraft.network.c2s;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public record UpdateWandFocusC2SPacket(ItemVariant pickedFocus) implements CustomPayload {
    public static final CustomPayload.Id<UpdateWandFocusC2SPacket> ID = new Id<>(wid("update_wand_focus"));
    public static final PacketCodec<RegistryByteBuf, UpdateWandFocusC2SPacket> PACKET_CODEC = ItemVariant.PACKET_CODEC
            .xmap(UpdateWandFocusC2SPacket::new, UpdateWandFocusC2SPacket::pickedFocus)
            .cast();

    @Override
    public Id<UpdateWandFocusC2SPacket> getId() {
        return ID;
    }

//    public UpdateWandFocusC2SPacket(PacketByteBuf buf) {
//        this(ItemVariant.fromPacket(buf));
//    }
//
//    @Override
//    public void write(PacketByteBuf buf) {
//        this.pickedFocus.toPacket(buf);
//    }
//
//    @Override
//    public PacketType<UpdateWandFocusC2SPacket> getType() {
//        return TYPE;
//    }
}
