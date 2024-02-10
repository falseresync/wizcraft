package dev.falseresync.wizcraft.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.common.recipe.WizcraftRecipes;
import dev.falseresync.wizcraft.compat.emi.recipe.LensedWorktableEmiRecipe;
import net.minecraft.util.Identifier;

public class WizcraftEmiPlugin implements EmiPlugin {
    public static final Identifier EMI_ATLAS
            = new Identifier(Wizcraft.MODID, "textures/block/worktable_top.png");
    public static final EmiStack WORKTABLE = EmiStack.of(WizcraftItems.WORKTABLE);
    public static final EmiRecipeCategory LENSED_WORKTABLE = new EmiRecipeCategory(
            new Identifier(Wizcraft.MODID, "lensed_worktable"),
            WORKTABLE,
            new EmiTexture(EMI_ATLAS, 0, 0, 16, 16));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(LENSED_WORKTABLE);
        registry.addWorkstation(LENSED_WORKTABLE, WORKTABLE);

        var recipeManager = registry.getRecipeManager();
        for (var recipeEntry : recipeManager.listAllOfType(WizcraftRecipes.LENSED_WORKTABLE)) {
            registry.addRecipe(new LensedWorktableEmiRecipe(recipeEntry.id(), recipeEntry.value()));
        }
    }
}
