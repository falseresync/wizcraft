package ru.falseresync.wizcraft.api.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.recipe.Ingredient;
import ru.falseresync.wizcraft.common.names.WizCodecNames;
import ru.falseresync.wizcraft.lib.CommonCodecs;

import java.util.*;

public record Composition(Ingredient ingredient, List<ElementAmount> elements) {
    public static final Codec<Composition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CommonCodecs.INGREDIENT.fieldOf(WizCodecNames.SOURCE).forGetter(Composition::ingredient),
            ElementAmount.CODEC.listOf().fieldOf(WizCodecNames.ELEMENTS).forGetter(Composition::elements)
    ).apply(instance, Composition::new));

    public Composition {
        if (Arrays.stream(ingredient.getMatchingStacks()).anyMatch(stack -> stack.getCount() > 1)) {
            throw new IllegalArgumentException("Element source can only have a count (amount) of 1");
        }
    }
}
