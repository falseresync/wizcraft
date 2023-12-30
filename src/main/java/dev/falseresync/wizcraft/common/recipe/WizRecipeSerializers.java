package dev.falseresync.wizcraft.common.recipe;

import dev.falseresync.wizcraft.lib.HasId;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizRecipeSerializers {
    public static final LensedWorktableRecipe.Serializer LENSED_WORKTABLE;
    private static final Map<Identifier, RecipeSerializer<?>> TO_REGISTER = new HashMap<>();

    static {
        LENSED_WORKTABLE = r(new LensedWorktableRecipe.Serializer());
    }

    private static <T extends RecipeSerializer<?> & HasId> T r(T serializer) {
        TO_REGISTER.put(serializer.getId(), serializer);
        return serializer;
    }

    public static void register(BiConsumer<Identifier, RecipeSerializer<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
