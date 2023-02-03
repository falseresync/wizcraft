package ru.falseresync.wizcraft.lib;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.recipe.Ingredient;

/**
 * @author RecourcefulLib
 */
public final class IngredientCodec {
    public static final Codec<Ingredient> CODEC = Codec.PASSTHROUGH.comapFlatMap(IngredientCodec::decodeIngredient, IngredientCodec::encodeIngredient);

    private IngredientCodec() {
    }

    private static DataResult<Ingredient> decodeIngredient(Dynamic<?> dynamic) {
        if ((Object) dynamic.convert(JsonOps.INSTANCE).getValue() instanceof JsonElement jsonElement) {
            return DataResult.success(Ingredient.fromJson(jsonElement));
        }
        return DataResult.error("Value was not an instance of JsonElement");
    }

    private static Dynamic<JsonElement> encodeIngredient(Ingredient ingredient) {
        return new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()).convert(JsonOps.COMPRESSED);
    }
}
