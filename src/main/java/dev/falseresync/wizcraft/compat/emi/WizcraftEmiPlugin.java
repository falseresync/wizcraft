package dev.falseresync.wizcraft.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.recipe.WizRecipes;
import dev.falseresync.wizcraft.compat.emi.recipe.LensedWorktableEmiRecipe;
import net.minecraft.util.Identifier;

public class WizcraftEmiPlugin implements EmiPlugin {
    public static final Identifier EMI_ATLAS
            = new Identifier(Wizcraft.MODID, "textures/block/energized_worktable_block.png");
    public static final EmiStack ENERGIZED_WORKTABLE = EmiStack.of(WizItems.ENERGIZED_WORKTABLE);
    public static final EmiRecipeCategory LENSED_WORKTABLE = new EmiRecipeCategory(
            new Identifier(Wizcraft.MODID, "lensed_worktable"),
            ENERGIZED_WORKTABLE,
            new EmiTexture(EMI_ATLAS, 0, 0, 16, 16));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(LENSED_WORKTABLE);
        registry.addWorkstation(LENSED_WORKTABLE, ENERGIZED_WORKTABLE);

        var recipeManager = registry.getRecipeManager();
        for (var recipeEntry : recipeManager.listAllOfType(WizRecipes.LENSED_WORKTABLE)) {
            registry.addRecipe(new LensedWorktableEmiRecipe(recipeEntry.id(), recipeEntry.value()));
        }
    }
}