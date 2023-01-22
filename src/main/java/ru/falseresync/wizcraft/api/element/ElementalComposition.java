package ru.falseresync.wizcraft.api.element;

import com.google.gson.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ElementalComposition(Ingredient ingredient, List<ElementAmount> elements) {
    public static class Manager {
        public static final Set<ElementalComposition> compositions;

        static {
            compositions = new HashSet<>();
        }
    }

    public static class Deserializer implements JsonDeserializer<ElementalComposition> {
        @Override
        public ElementalComposition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var compositionJsonObject = JsonHelper.asObject(json, "elemental composition");
            var ingredient = Ingredient.fromJson(compositionJsonObject.get("source"));
            var elements = new ArrayList<ElementAmount>();
            for (var elementJsonElement : JsonHelper.getArray(compositionJsonObject, "elements"))
                elements.add(context.deserialize(elementJsonElement, ElementAmount.class));
            if (elements.isEmpty())
                throw new JsonSyntaxException("Elements cannot be empty");
            return new ElementalComposition(ingredient, elements);
        }
    }
}
