package ru.falseresync.wizcraft.common.element;

import com.google.gson.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.lib.names.WizJsonNames;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ElementalComposition(Ingredient ingredient, List<ElementAmount> elements) {
    public static class Manager {
        public static final Set<ElementalComposition> COMPOSITIONS;

        static {
            COMPOSITIONS = new HashSet<>();
        }
    }

    public static class Deserializer implements JsonDeserializer<ElementalComposition> {
        @Override
        public ElementalComposition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var compositionJsonObject = JsonHelper.asObject(json, WizJsonNames.ELEMENTAL_COMPOSITION);

            var ingredient = Ingredient.fromJson(compositionJsonObject.get(WizJsonNames.SOURCE));
            for (var matchingStack : ingredient.getMatchingStacks()) {
                if (matchingStack.getCount() > 1) {
                    throw new JsonSyntaxException("Element source can only have a count (amount) of 1");
                }
            }

            var elements = new ArrayList<ElementAmount>();
            for (var elementJsonElement : JsonHelper.getArray(compositionJsonObject, WizJsonNames.ELEMENTS)) {
                elements.add(context.deserialize(elementJsonElement, ElementAmount.class));
            }
            if (elements.isEmpty()) {
                throw new JsonSyntaxException("Elements cannot be empty");
            }

            return new ElementalComposition(ingredient, elements);
        }
    }
}
