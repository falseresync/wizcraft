package falseresync.wizcraft.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import falseresync.wizcraft.common.recipe.CountableIngredient;
import falseresync.wizcraft.common.recipe.CrucibleRecipe;
import falseresync.wizcraft.compat.emi.WizcraftEmiPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class CrucibleEmiRecipe implements EmiRecipe {
    public static final EmiTexture CRUCIBLE_TEX =
            new EmiTexture(wid("textures/gui/recipe/crucible.png"), 0, 0, 32, 32, 32, 32, 32, 32);
    public static final EmiTexture ARROW_TEX =
            new EmiTexture(wid("textures/gui/recipe/arrow_right.png"), 0, 0, 16, 16, 16, 16, 16, 16);

    protected final RecipeHolder<CrucibleRecipe> backingRecipe;
    protected final ResourceLocation id;
    protected final EmiStack result;
    protected final List<EmiIngredient> inputs;

    public CrucibleEmiRecipe(RecipeHolder<CrucibleRecipe> recipeEntry) {
        this.backingRecipe = recipeEntry;
        this.id = recipeEntry.id();
        var recipe = recipeEntry.value();
        this.result = EmiStack.of(recipe.result());
        this.inputs = recipe.ingredients().stream().map(it -> {
            var emiIngredient = EmiIngredient.of(it);
            if (it.getCustomIngredient() instanceof CountableIngredient countableIngredient) {
                return emiIngredient.setAmount(countableIngredient.count());
            }
            return emiIngredient;
        }).toList();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return WizcraftEmiPlugin.CATEGORY_LENSED_WORKTABLE;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(result);
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final var margin = 2;
        final var slotW = 18;
        final var xStart = 50 - (inputs.size() * slotW + (inputs.size() - 1) * margin) / 2;

        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), xStart + i * (slotW + margin), margin);
        }

        widgets.addTexture(CRUCIBLE_TEX, 50 - 16, slotW + margin + margin);
        widgets.addTexture(ARROW_TEX, 60 + slotW / 2 + margin, slotW + margin + 16 - 8);
        widgets.addSlot(this.result, 60 + slotW / 2 + margin + slotW + margin, slotW + margin + 16 - 9).recipeContext(this);
    }

    @Nullable
    @Override
    public RecipeHolder<CrucibleRecipe> getBackingRecipe() {
        return backingRecipe;
    }
}
