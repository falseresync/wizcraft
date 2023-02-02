package ru.falseresync.wizcraft.api.element;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.api.NbtSerializable;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.lib.names.WizNbtNames;

public record ElementAmount(Element element, long amount) implements NbtSerializable {
    @Override
    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
        nbt.putString(WizNbtNames.ELEMENT, element.toString());
        nbt.putLong(WizNbtNames.AMOUNT, amount);
        return nbt;
    }

    public static ElementAmount fromNbt(NbtCompound nbt) {
        return new ElementAmount(
                WizRegistries.ELEMENT.get(new Identifier(nbt.getString(WizNbtNames.ELEMENT))),
                nbt.getLong(WizNbtNames.AMOUNT)
        );
    }

    @Override
    public String toString() {
        return "%d x %s".formatted(amount, element);
    }
}
