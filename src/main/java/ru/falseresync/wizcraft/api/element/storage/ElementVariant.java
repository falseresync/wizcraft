package ru.falseresync.wizcraft.api.element.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import ru.falseresync.wizcraft.api.element.Element;

public class ElementVariant implements TransferVariant<Element> {
    @Override
    public boolean isBlank() {
        return false;
    }

    @Override
    public Element getObject() {
        return null;
    }

    @Override
    public @Nullable NbtCompound getNbt() {
        return null;
    }

    @Override
    public NbtCompound toNbt() {
        return null;
    }

    @Override
    public void toPacket(PacketByteBuf buf) {

    }
}
