package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.recipe.CountableIngredient;
import falseresync.wizcraft.datagen.recipe.CrucibleRecipeJsonBuilder;
import falseresync.wizcraft.datagen.recipe.LensedWorktableRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class WizcraftCustomRecipeProvider extends FabricRecipeProvider {
    public WizcraftCustomRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Custom Recipes";
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateLensedWorktable(exporter);
        generateCrucible(exporter);
    }

    private void generateLensedWorktable(RecipeOutput exporter) {
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.STARSHOOTER_FOCUS, Ingredient.of(Items.COPPER_BLOCK))
                .pedestalInput(Ingredient.of(ConventionalItemTags.GOLD_INGOTS))
                .pedestalInput(Ingredient.of(Items.FIRE_CHARGE))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.CHARGING_FOCUS, Ingredient.of(Items.AMETHYST_BLOCK))
                .pedestalInput(Ingredient.of(ConventionalItemTags.PRISMARINE_GEMS))
                .pedestalInput(Ingredient.of(ConventionalItemTags.DIAMOND_GEMS))
                .pedestalInput(Ingredient.of(ConventionalItemTags.PRISMARINE_GEMS))
                .pedestalInput(Ingredient.of(ConventionalItemTags.DIAMOND_GEMS))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.LIGHTNING_FOCUS, Ingredient.of(Items.DIAMOND_BLOCK))
                .pedestalInput(Ingredient.of(Items.LIGHTNING_ROD))
                .pedestalInput(Ingredient.of(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.COMET_WARP_FOCUS, Ingredient.of(Items.SLIME_BLOCK))
                .pedestalInput(Ingredient.of(ConventionalItemTags.ENDER_PEARLS))
                .pedestalInput(Ingredient.of(Items.CHORUS_FRUIT))
                .pedestalInput(Ingredient.of(ConventionalItemTags.ENDER_PEARLS))
                .pedestalInput(Ingredient.of(Items.CHORUS_FRUIT))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.ENERGY_VEIL_FOCUS, Ingredient.of(Items.DIAMOND_BLOCK))
                .pedestalInput(Ingredient.of(ConventionalItemTags.SHIELD_TOOLS))
                .pedestalInput(Ingredient.of(Items.PHANTOM_MEMBRANE))
                .pedestalInput(Ingredient.of(ConventionalItemTags.SHIELD_TOOLS))
                .pedestalInput(Ingredient.of(Items.PHANTOM_MEMBRANE))
                .offerTo(exporter);
    }

    private void generateCrucible(RecipeOutput exporter) {
        new CrucibleRecipeJsonBuilder(WizcraftItems.METALLIZED_STICK)
                .input(Ingredient.of(Items.STICK))
                .input(Ingredient.of(ConventionalItemTags.COPPER_INGOTS))
                .offerTo(exporter);
        new CrucibleRecipeJsonBuilder(WizcraftItems.LENS)
                .input(new CountableIngredient(Ingredient.of(ConventionalItemTags.DIAMOND_GEMS), 2).toVanilla())
                .input(new CountableIngredient(Ingredient.of(ConventionalItemTags.AMETHYST_GEMS), 6).toVanilla())
                .input(Ingredient.of(ConventionalItemTags.GLASS_BLOCKS_COLORLESS))
                .offerTo(exporter);
        new CrucibleRecipeJsonBuilder(WizcraftItems.CHARGE_SHELL)
                .input(Ingredient.of(ConventionalItemTags.DIAMOND_GEMS))
                .input(Ingredient.of(ConventionalItemTags.AMETHYST_GEMS))
                .input(Ingredient.of(ConventionalItemTags.REDSTONE_DUSTS))
                .input(Ingredient.of(Items.GHAST_TEAR))
                .input(Ingredient.of(Items.NAUTILUS_SHELL))
                .offerTo(exporter);
    }
}
