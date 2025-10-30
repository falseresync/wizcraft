package falseresync.wizcraft.datagen.recipe;

import falseresync.wizcraft.common.recipe.CrucibleRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class CrucibleRecipeJsonBuilder {
    private final Item result;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();

    public CrucibleRecipeJsonBuilder(ItemLike result) {
        this.result = result.asItem();
    }

    public CrucibleRecipeJsonBuilder input(Ingredient input) {
        ingredients.add(input);
        return this;
    }

    public void offerTo(RecipeOutput exporter, ResourceLocation id) {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("A Crucible recipe should contain at least one input");
        }
        var recipe = new CrucibleRecipe(new ItemStack(result), ingredients);
        exporter.accept(id, recipe, null);
    }

    public void offerTo(RecipeOutput exporter) {
        offerTo(exporter, BuiltInRegistries.ITEM.getKey(result).withPrefix("crucible/"));
    }
}
