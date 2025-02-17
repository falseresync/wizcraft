package falseresync.wizcraft.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.recipe.WizcraftRecipes;
import falseresync.wizcraft.compat.emi.recipe.CrucibleEmiRecipe;
import falseresync.wizcraft.compat.emi.recipe.LensedWorktableEmiRecipe;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftEmiPlugin implements EmiPlugin {
    public static final Identifier EMI_ATLAS = wid("textures/block/worktable_top.png");

    public static final EmiStack WORKSTATION_WORKTABLE = EmiStack.of(WizcraftItems.WORKTABLE);
    public static final EmiStack WORKSTATION_CRUCIBLE = EmiStack.of(Items.CAULDRON);

    public static final EmiRecipeCategory CATEGORY_LENSED_WORKTABLE =
            new EmiRecipeCategory(wid("lensed_worktable"), WORKSTATION_WORKTABLE, new EmiTexture(EMI_ATLAS, 0, 0, 16, 16));
    public static final EmiRecipeCategory CATEGORY_CRUCIBLE =
            new EmiRecipeCategory(wid("crucible"), WORKSTATION_CRUCIBLE, new EmiTexture(EMI_ATLAS, 0, 0, 16, 16));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY_LENSED_WORKTABLE);
        registry.addCategory(CATEGORY_CRUCIBLE);

        registry.addWorkstation(CATEGORY_LENSED_WORKTABLE, WORKSTATION_WORKTABLE);
        registry.addWorkstation(CATEGORY_CRUCIBLE, WORKSTATION_CRUCIBLE);

        var recipeManager = registry.getRecipeManager();
        for (var recipeEntry : recipeManager.listAllOfType(WizcraftRecipes.LENSED_WORKTABLE)) {
            registry.addRecipe(new LensedWorktableEmiRecipe(recipeEntry));
        }
        for (var recipeEntry : recipeManager.listAllOfType(WizcraftRecipes.CRUCIBLE)) {
            registry.addRecipe(new CrucibleEmiRecipe(recipeEntry));
        }
    }
}
