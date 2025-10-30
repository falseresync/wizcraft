package falseresync.wizcraft.datagen.recipe;

import falseresync.wizcraft.common.recipe.LensedWorktableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class LensedWorktableRecipeJsonBuilder {
    private final Item result;
    private final Ingredient worktableInput;
    private final NonNullList<Ingredient> pedestalInputs = NonNullList.create();

    public LensedWorktableRecipeJsonBuilder(ItemLike result, Ingredient worktableInput) {
        this.result = result.asItem();
        this.worktableInput = worktableInput;
    }

    public LensedWorktableRecipeJsonBuilder pedestalInput(Ingredient pedestalInput) {
        pedestalInputs.add(pedestalInput);
        return this;
    }

    public void offerTo(RecipeOutput exporter, ResourceLocation id) {
        if (pedestalInputs.isEmpty()) {
            throw new IllegalStateException("A Lensed worktable recipe should contain at least one pedestal input");
        }
        var recipe = new LensedWorktableRecipe(new ItemStack(result), worktableInput, pedestalInputs);
        exporter.accept(id, recipe, null);
    }

    public void offerTo(RecipeOutput exporter) {
        offerTo(exporter, BuiltInRegistries.ITEM.getKey(result).withPrefix("lensed_worktable/"));
    }
}
