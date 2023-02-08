package ru.falseresync.wizcraft.api.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.common.init.WizElements;
import ru.falseresync.wizcraft.common.storage.ElementVariantImpl;

public interface ElementVariant extends TransferVariant<Element> {
    static ElementVariant blank() {
        return of(WizElements.EMPTY);
    }

    static ElementVariant of(Element element) {
        return of(element, null);
    }

    static ElementVariant of(Element element, @Nullable NbtCompound nbt) {
        return ElementVariantImpl.of(element, nbt);
    }

    default Element getElement() {
        return getObject();
    }

    static ElementVariant fromNbt(NbtCompound compound) {
        return ElementVariantImpl.fromNbt(compound);
    }

    static ElementVariant fromPacket(PacketByteBuf buf) {
        return ElementVariantImpl.fromPacket(buf);
    }
}
