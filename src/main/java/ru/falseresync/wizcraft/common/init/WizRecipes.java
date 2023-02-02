package ru.falseresync.wizcraft.common.init;

import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.common.recipe.CatalyticCondensationRecipe;
import ru.falseresync.wizcraft.lib.IdUtil;

public class WizRecipes {
    public static final RecipeType<CatalyticCondensationRecipe> CATALYTIC_CONDENSATION;

    static {
        CATALYTIC_CONDENSATION = new RecipeType<>() {
        };
    }

    public static void register() {
        Registry.register(Registries.RECIPE_TYPE, IdUtil.id("catalytic_condensation"), CATALYTIC_CONDENSATION);

        Registry.register(Registries.RECIPE_SERIALIZER, IdUtil.id("catalytic_condensation"), CatalyticCondensationRecipe.Serializer.INSTANCE);
    }
}
