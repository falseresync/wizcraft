package falseresync.wizcraft.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import falseresync.wizcraft.common.recipe.LensedWorktableRecipe;
import falseresync.wizcraft.compat.emi.WizcraftEmiPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class LensedWorktableEmiRecipe implements EmiRecipe {
    public static final EmiTexture WAND_TEX = new EmiTexture(
            wid("textures/item/wand.png"),
            0, 0, 16, 16, 16, 16, 16, 16);
    public static final EmiTexture ARROW_TEX = new EmiTexture(
            wid("textures/gui/recipe/arrow_right.png"),
            0, 0, 16, 16, 16, 16, 16, 16);

    protected final RecipeHolder<LensedWorktableRecipe> backingRecipe;
    protected final ResourceLocation id;
    protected final EmiStack result;
    protected final EmiIngredient worktableInput;
    protected final List<EmiIngredient> pedestalInputs;
    protected final List<EmiIngredient> allInputs;

    public LensedWorktableEmiRecipe(RecipeHolder<LensedWorktableRecipe> recipeEntry) {
        this.backingRecipe = recipeEntry;
        this.id = recipeEntry.id();
        var recipe = recipeEntry.value();
        this.result = EmiStack.of(recipe.getResult());
        this.worktableInput = EmiIngredient.of(recipe.getWorktableInput());
        this.pedestalInputs = recipe.getPedestalInputs().stream().map(EmiIngredient::of).toList();
        this.allInputs = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
    }


    @Override
    public EmiRecipeCategory getCategory() {
        return WizcraftEmiPlugin.CATEGORY_LENSED_WORKTABLE;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.allInputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.result);
    }

    @Override
    public int getDisplayWidth() {
        return 126;
    }

    @Override
    public int getDisplayHeight() {
        return 72;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.worktableInput, 27, 27);
        switch (this.pedestalInputs.size()) {
            case 1 -> {
                widgets.addSlot(this.pedestalInputs.getFirst(), 27, 0);
                widgets.addSlot(54, 27);
                widgets.addSlot(27, 54);
                widgets.addSlot(0, 27);
            }
            case 2 -> {
                widgets.addSlot(this.pedestalInputs.get(0), 27, 0);
                widgets.addSlot(54, 27);
                widgets.addSlot(this.pedestalInputs.get(1), 27, 54);
                widgets.addSlot(0, 27);
            }
            case 3 -> {
                widgets.addSlot(this.pedestalInputs.get(0), 27, 0);
                widgets.addSlot(this.pedestalInputs.get(1), 54, 27);
                widgets.addSlot(this.pedestalInputs.get(2), 27, 54);
                widgets.addSlot(27, 0);
            }
            case 4 -> {
                widgets.addSlot(this.pedestalInputs.get(0), 27, 0);
                widgets.addSlot(this.pedestalInputs.get(1), 54, 27);
                widgets.addSlot(this.pedestalInputs.get(2), 27, 54);
                widgets.addSlot(this.pedestalInputs.get(3), 0, 27);
            }
        }
        widgets.addTexture(WAND_TEX, 81, 9);
        widgets.addTexture(ARROW_TEX, 81, 27);
        widgets.addSlot(this.result, 108, 27).recipeContext(this);
    }

    @Nullable
    @Override
    public RecipeHolder<LensedWorktableRecipe> getBackingRecipe() {
        return backingRecipe;
    }
}
