package dev.falseresync.wizcraft.network.c2s;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.HasId;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record UpdateWandFocusC2SPacket(ItemVariant pickedFocus) implements FabricPacket, HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "update_wand_focus");
    public static final PacketType<UpdateWandFocusC2SPacket> TYPE = PacketType.create(ID, UpdateWandFocusC2SPacket::new);

    public UpdateWandFocusC2SPacket(PacketByteBuf buf) {
        this(ItemVariant.fromPacket(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        this.pickedFocus.toPacket(buf);
    }

    @Override
    public PacketType<UpdateWandFocusC2SPacket> getType() {
        return TYPE;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
