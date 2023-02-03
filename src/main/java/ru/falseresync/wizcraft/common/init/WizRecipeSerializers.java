package ru.falseresync.wizcraft.common.init;

import net.minecraft.recipe.RecipeSerializer;
import ru.falseresync.wizcraft.common.recipe.CatalyticCondensationRecipe;
import ru.falseresync.wizcraft.lib.registry.RegistryObject;

public class WizRecipeSerializers {
    public static final @RegistryObject RecipeSerializer<CatalyticCondensationRecipe> CATALYTIC_CONDENSATION = CatalyticCondensationRecipe.Serializer.INSTANCE;
}
