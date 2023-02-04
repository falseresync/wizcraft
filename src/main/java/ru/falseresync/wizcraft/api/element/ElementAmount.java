package ru.falseresync.wizcraft.api.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.common.names.WizCodecNames;

import java.util.Objects;

public record ElementAmount(Element element, long amount) {
    public static final Codec<ElementAmount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WizRegistries.ELEMENT.getCodec().fieldOf(WizCodecNames.ELEMENT).forGetter(ElementAmount::element),
            Codec.LONG.fieldOf(WizCodecNames.AMOUNT).forGetter(ElementAmount::amount)
    ).apply(instance, ElementAmount::new));

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementAmount that = (ElementAmount) o;
        return amount == that.amount && Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, amount);
    }

    @Override
    public String toString() {
        return "%d x %s".formatted(amount, element);
    }
}
