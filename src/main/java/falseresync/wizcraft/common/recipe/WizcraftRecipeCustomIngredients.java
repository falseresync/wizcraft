package falseresync.wizcraft.common.recipe;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class WizcraftRecipeCustomIngredients {
    public static final CountableIngredient.Serializer COUNTABLE_INGREDIENT_SERIALIZER = new CountableIngredient.Serializer();

    public static void init() {
        CustomIngredientSerializer.register(COUNTABLE_INGREDIENT_SERIALIZER);
    }
}
