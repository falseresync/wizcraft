package ru.falseresync.wizcraft.lib;

import com.mojang.serialization.Codec;
import net.minecraft.recipe.Ingredient;

public final class CommonCodecs {
    public static final Codec<Ingredient> INGREDIENT = CodecUtil.passthrough(Ingredient::toJson, Ingredient::fromJson);

    private CommonCodecs() {
    }
}
