package falseresync.wizcraft.common.recipe;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class WizcraftRecipeCustomIngredients {
    public static final CrucibleRecipeIngredient.Serializer CRUCIBLE_RECIPE_INGREDIENT_SERIALIZER = new CrucibleRecipeIngredient.Serializer();

    public static void init() {
        CustomIngredientSerializer.register(CRUCIBLE_RECIPE_INGREDIENT_SERIALIZER);
    }
}
