package dev.falseresync.wizcraft.network.c2s;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.HasId;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record UpdateSkyWandC2SPacket(ItemVariant pickedFocus) implements FabricPacket, HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "update_sky_wand_focus");
    public static final PacketType<UpdateSkyWandC2SPacket> TYPE = PacketType.create(ID, UpdateSkyWandC2SPacket::new);

    public UpdateSkyWandC2SPacket(PacketByteBuf buf) {
        this(ItemVariant.fromPacket(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        this.pickedFocus.toPacket(buf);
    }

    @Override
    public PacketType<UpdateSkyWandC2SPacket> getType() {
        return TYPE;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
