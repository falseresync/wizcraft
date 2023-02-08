package ru.falseresync.wizcraft.common.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.api.storage.ElementVariant;
import ru.falseresync.wizcraft.common.Wizcraft;
import ru.falseresync.wizcraft.common.init.WizElements;
import ru.falseresync.wizcraft.common.names.WizCodecNames;
import ru.falseresync.wizcraft.common.names.WizNbtNames;

import java.util.Objects;

public final class ElementVariantImpl implements ElementVariant {
    private final Element element;
    private final @Nullable NbtCompound nbt;
    private final int hashCode;

    public ElementVariantImpl(Element element, @Nullable NbtCompound nbt) {
        this.element = element;
        this.nbt = nbt == null ? null : nbt.copy(); // defensive copy
        this.hashCode = Objects.hash(element, nbt);
    }

    public static ElementVariant of(Element element, @Nullable NbtCompound nbt) {
        Objects.requireNonNull(element, "Element may not be null.");

        if (nbt == null || element == WizElements.EMPTY) {
            // Use the cached variant
            return element.getCachedVariant();
        } else {
            return new ElementVariantImpl(element, nbt);
        }
    }

    public static ElementVariant fromNbt(NbtCompound compound) {
        try {
            var fluid = WizRegistries.ELEMENT.get(new Identifier(compound.getString(WizCodecNames.ELEMENT)));
            var nbt = compound.contains(WizNbtNames.TAG) ? compound.getCompound(WizNbtNames.TAG) : null;
            return of(fluid, nbt);
        } catch (RuntimeException runtimeException) {
            Wizcraft.LOGGER.debug("Tried to load an invalid ElementVariant from NBT: {}", compound, runtimeException);
            return ElementVariant.blank();
        }
    }

    public static ElementVariant fromPacket(PacketByteBuf buf) {
        if (!buf.readBoolean()) {
            return ElementVariant.blank();
        } else {
            var element = WizRegistries.ELEMENT.get(buf.readVarInt());
            var nbt = buf.readNbt();
            return of(element, nbt);
        }
    }

    @Override
    public boolean isBlank() {
        return element == WizElements.EMPTY;
    }

    @Override
    public Element getObject() {
        return element;
    }

    @Override
    public @Nullable NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound result = new NbtCompound();
        result.putString(WizCodecNames.ELEMENT, WizRegistries.ELEMENT.getId(element).toString());

        if (nbt != null) {
            result.put(WizNbtNames.TAG, nbt.copy());
        }

        return result;
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        if (isBlank()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeVarInt(WizRegistries.ELEMENT.getRawId(element));
            buf.writeNbt(nbt);
        }
    }

    @Override
    public String toString() {
        return "ElementVariantImpl{element=" + element + ", tag=" + nbt + '}';
    }

    @Override
    public boolean equals(Object o) {
        // succeed fast with == check
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var elementVariant = (ElementVariantImpl) o;
        // fail fast with hash code
        return hashCode == elementVariant.hashCode && element == elementVariant.element && nbtMatches(elementVariant.nbt);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
