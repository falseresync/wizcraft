package falseresync.wizcraft.compat.lavender;

import falseresync.wizcraft.common.recipe.CountableIngredient;
import falseresync.wizcraft.common.recipe.CrucibleRecipe;
import falseresync.wizcraft.common.recipe.WizcraftRecipes;
import io.wispforest.lavender.client.LavenderBookScreen;
import io.wispforest.lavender.md.compiler.BookCompiler;
import io.wispforest.lavender.md.features.RecipeFeature;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static falseresync.wizcraft.common.Wizcraft.wid;

@Environment(EnvType.CLIENT)
public class WizcraftLavenderPlugin {
    public static final ResourceLocation CRUCIBLE_TEX = wid("textures/gui/recipe/crucible.png");
    public static final ResourceLocation ARROW_TEX = wid("textures/gui/recipe/arrow_right.png");

    public static final ResourceLocation GRIMOIRE_ID = wid("grimoire");

    public static void init() {
        UIParsing.registerFactory(wid("countable-item-list"), element -> new CountableItemListComponent());

        var params = Map.of(
                "arrow-tex", ARROW_TEX.toString(),
                "crucible-tex", CRUCIBLE_TEX.toString()
        );

        LavenderBookScreen.registerRecipePreviewBuilder(GRIMOIRE_ID, WizcraftRecipes.CRUCIBLE, new RecipeFeature.RecipePreviewBuilder<>() {
            @Override
            public @NotNull Component buildRecipePreview(BookCompiler.ComponentSource componentSource, RecipeHolder<CrucibleRecipe> recipeEntry) {
                var recipe = recipeEntry.value();
                var recipeComponent = componentSource.template(UIModelLoader.get(GRIMOIRE_ID), ParentComponent.class, "crucible-recipe", params);

                populateCountableIngredientsRemoveUnused(recipe.ingredients(), recipeComponent.childById(ParentComponent.class, "ingredients"));
                recipeComponent.childById(ItemComponent.class, "result").stack(recipe.result());

                return recipeComponent;
            }

            private void populateCountableIngredientsRemoveUnused(List<Ingredient> ingredients, ParentComponent componentContainer) {
                var items = componentContainer.children().stream()
                        .flatMap(it -> it instanceof CountableItemListComponent entry ? Stream.of(entry) : Stream.empty())
                        .toList();
                for (int i = 0; i < ingredients.size(); i++) {
                    var ingredient = ingredients.get(i);
                    if (ingredient.getCustomIngredient() instanceof CountableIngredient countableIngredient) {
                        items.get(i).countableIngredient(countableIngredient);
                    } else {
                        items.get(i).ingredient(ingredient);
                    }
                }

                if (items.size() - ingredients.size() > 0) {
                    var last = items.size();
                    for (int i = ingredients.size(); i < last; i++) {
                        componentContainer.removeChild(items.get(i));
                    }
                }
            }
        });
    }


}
