package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.recipe.CountableIngredient;
import falseresync.wizcraft.datagen.recipe.CrucibleRecipeJsonBuilder;
import falseresync.wizcraft.datagen.recipe.LensedWorktableRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WizcraftCustomRecipeProvider extends FabricRecipeProvider {
    public WizcraftCustomRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Custom Recipes";
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateLensedWorktable(exporter);
        generateCrucible(exporter);
    }

    private void generateLensedWorktable(RecipeExporter exporter) {
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.STARSHOOTER_FOCUS, Ingredient.ofItems(Items.COPPER_BLOCK))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS))
                .pedestalInput(Ingredient.ofItems(Items.FIRE_CHARGE))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.CHARGING_FOCUS, Ingredient.ofItems(Items.AMETHYST_BLOCK))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.PRISMARINE_GEMS))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.DIAMOND_GEMS))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.PRISMARINE_GEMS))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.DIAMOND_GEMS))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.LIGHTNING_FOCUS, Ingredient.ofItems(Items.DIAMOND_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.LIGHTNING_ROD))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.COMET_WARP_FOCUS, Ingredient.ofItems(Items.SLIME_BLOCK))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.ENDER_PEARLS))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.ENDER_PEARLS))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .offerTo(exporter);
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.ENERGY_VEIL_FOCUS, Ingredient.ofItems(Items.DIAMOND_BLOCK))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.SHIELD_TOOLS))
                .pedestalInput(Ingredient.ofItems(Items.PHANTOM_MEMBRANE))
                .pedestalInput(Ingredient.fromTag(ConventionalItemTags.SHIELD_TOOLS))
                .pedestalInput(Ingredient.ofItems(Items.PHANTOM_MEMBRANE))
                .offerTo(exporter);
    }

    private void generateCrucible(RecipeExporter exporter) {
        new CrucibleRecipeJsonBuilder(WizcraftItems.METALLIZED_STICK)
                .input(Ingredient.ofItems(Items.STICK))
                .input(Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS))
                .offerTo(exporter);
        new CrucibleRecipeJsonBuilder(WizcraftItems.LENS)
                .input(new CountableIngredient(Ingredient.fromTag(ConventionalItemTags.DIAMOND_GEMS), 2).toVanilla())
                .input(new CountableIngredient(Ingredient.fromTag(ConventionalItemTags.AMETHYST_GEMS), 6).toVanilla())
                .input(Ingredient.fromTag(ConventionalItemTags.GLASS_BLOCKS_COLORLESS))
                .offerTo(exporter);
        new CrucibleRecipeJsonBuilder(WizcraftItems.CHARGE_SHELL)
                .input(Ingredient.fromTag(ConventionalItemTags.DIAMOND_GEMS))
                .input(Ingredient.fromTag(ConventionalItemTags.AMETHYST_GEMS))
                .input(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS))
                .input(Ingredient.ofItems(Items.GHAST_TEAR))
                .input(Ingredient.ofItems(Items.NAUTILUS_SHELL))
                .offerTo(exporter);
    }
}
