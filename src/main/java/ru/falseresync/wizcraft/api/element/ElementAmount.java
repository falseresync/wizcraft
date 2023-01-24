package ru.falseresync.wizcraft.api.element;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.api.NbtSerializable;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.data.WizNbtConstants;

public record ElementAmount(Element element, long amount) implements NbtSerializable {
    @Override
    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
        nbt.putString(WizNbtConstants.ELEMENT, element.toString());
        nbt.putLong(WizNbtConstants.AMOUNT, amount);
        return nbt;
    }

    public static ElementAmount fromNbt(NbtCompound nbt) {
        return new ElementAmount(
                WizRegistries.ELEMENT.get(new Identifier(nbt.getString(WizNbtConstants.ELEMENT))),
                nbt.getLong(WizNbtConstants.AMOUNT)
        );
    }

    @Override
    public String toString() {
        return "%d x %s".formatted(amount, element);
    }
}
