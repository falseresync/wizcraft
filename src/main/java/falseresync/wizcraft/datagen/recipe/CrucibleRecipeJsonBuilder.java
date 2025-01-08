package falseresync.wizcraft.datagen.recipe;

import falseresync.wizcraft.common.recipe.CrucibleRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class CrucibleRecipeJsonBuilder {
    private final Item result;
    private final DefaultedList<Ingredient> ingredients = DefaultedList.of();

    public CrucibleRecipeJsonBuilder(ItemConvertible result) {
        this.result = result.asItem();
    }

    public CrucibleRecipeJsonBuilder input(Ingredient input) {
        ingredients.add(input);
        return this;
    }

    public void offerTo(RecipeExporter exporter, Identifier id) {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("A Crucible recipe should contain at least one input");
        }
        var recipe = new CrucibleRecipe(new ItemStack(result), ingredients);
        exporter.accept(id, recipe, null);
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, Registries.ITEM.getId(result).withPrefixedPath("crucible/"));
    }
}
