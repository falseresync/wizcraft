package falseresync.wizcraft.datagen.recipe;

import falseresync.wizcraft.common.recipe.LensedWorktableRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class LensedWorktableRecipeJsonBuilder {
    private final Item result;
    private final Ingredient worktableInput;
    private final DefaultedList<Ingredient> pedestalInputs = DefaultedList.of();

    public LensedWorktableRecipeJsonBuilder(ItemConvertible result, Ingredient worktableInput) {
        this.result = result.asItem();
        this.worktableInput = worktableInput;
    }

    public LensedWorktableRecipeJsonBuilder pedestalInput(Ingredient pedestalInput) {
        pedestalInputs.add(pedestalInput);
        return this;
    }

    public void offerTo(RecipeExporter exporter, Identifier id) {
        if (pedestalInputs.isEmpty()) {
            throw new IllegalStateException("A Lensed worktable recipe should contain at least one pedestal input");
        }
        var recipe = new LensedWorktableRecipe(new ItemStack(result), worktableInput, pedestalInputs);
        exporter.accept(id, recipe, null);
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, Registries.ITEM.getId(result).withPrefixedPath("lensed_worktable/"));
    }
}
