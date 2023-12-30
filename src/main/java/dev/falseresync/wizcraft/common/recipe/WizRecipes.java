package dev.falseresync.wizcraft.common.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizRecipes {
    public static final RecipeType<LensedWorktableRecipe> LENSED_WORKTABLE;
    private static final Map<Identifier, RecipeType<?>> TO_REGISTER = new HashMap<>();

    static {
        LENSED_WORKTABLE = r(LensedWorktableRecipe.ID);
    }

    private static <T extends Recipe<?>> RecipeType<T> r(Identifier id) {
        var type = new RecipeType<T>() {};
        TO_REGISTER.put(id, type);
        return type;
    }

    public static void register(BiConsumer<Identifier, RecipeType<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
