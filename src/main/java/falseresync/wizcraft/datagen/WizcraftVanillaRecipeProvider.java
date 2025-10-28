package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.item.focus.FocusPlating;
import falseresync.wizcraft.datagen.recipe.CustomSmithingTransformRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Block;
import net.minecraft.component.ComponentChanges;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class WizcraftVanillaRecipeProvider extends FabricRecipeProvider {
    public WizcraftVanillaRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Vanilla Recipes";
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateCrafting(exporter);
        generateFocusPlating(exporter);
    }

    private void generateCrafting(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.MORTAR_AND_PESTLE)
                .input('i', ConventionalItemTags.IRON_NUGGETS)
                .input('f', Items.FLINT)
                .input('s', Items.SMOOTH_STONE_SLAB)
                .pattern("i")
                .pattern("f")
                .pattern("s")
                .criterion("unlock_right_away", TickCriterion.Conditions.createTick())
                .offerTo(exporter, item(WizcraftItems.MORTAR_AND_PESTLE));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.GRIMOIRE)
                .input(ConventionalItemTags.DIAMOND_GEMS)
                .input(ConventionalItemTags.AMETHYST_GEMS)
                .input(ConventionalItemTags.REDSTONE_DUSTS)
                .input(Items.BOOK)
                .input(WizcraftItems.MORTAR_AND_PESTLE)
                .criterion("unlock_right_away", TickCriterion.Conditions.createTick())
                .offerTo(exporter, item(WizcraftItems.GRIMOIRE));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.WAND_CORE)
                .input(ConventionalItemTags.DIAMOND_GEMS)
                .input(ConventionalItemTags.AMETHYST_GEMS)
                .input(ConventionalItemTags.REDSTONE_DUSTS)
                .input(Ingredient.ofItems(Items.SLIME_BALL, Items.HONEY_BOTTLE))
                .input(WizcraftItems.MORTAR_AND_PESTLE)
                .criterion("has_diamond", conditionsFromTag(ConventionalItemTags.DIAMOND_GEMS))
                .criterion("has_amethyst", conditionsFromTag(ConventionalItemTags.AMETHYST_GEMS))
                .offerTo(exporter, item(WizcraftItems.WAND_CORE));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.WAND)
                .input('w', WizcraftItems.WAND_CORE)
                .input('g', ConventionalItemTags.GOLD_INGOTS)
                .input('s', WizcraftItems.METALLIZED_STICK)
                .pattern("  w")
                .pattern(" g ")
                .pattern("s  ")
                .criterion("has_diamond", conditionsFromTag(ConventionalItemTags.DIAMOND_GEMS))
                .criterion("has_amethyst", conditionsFromTag(ConventionalItemTags.AMETHYST_GEMS))
                .offerTo(exporter, item(WizcraftItems.WAND));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.TRUESEER_GOGGLES)
                .input('g', ConventionalItemTags.GOLD_NUGGETS)
                .input('h', Items.CHAINMAIL_HELMET)
                .input('p', Items.PHANTOM_MEMBRANE)
                .input('c', ConventionalItemTags.PRISMARINE_GEMS)
                .pattern("g g")
                .pattern("php")
                .pattern("c c")
                .criterion("has_phantom_membrane", conditionsFromItem(Items.PHANTOM_MEMBRANE))
                .criterion("has_prismarine", conditionsFromTag(ConventionalItemTags.PRISMARINE_GEMS))
                .offerTo(exporter, item(WizcraftItems.TRUESEER_GOGGLES));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.FOCUSES_BELT)
                .input('l', ConventionalItemTags.LEATHERS)
                .input('t', Items.TURTLE_HELMET)
                .pattern("ltl")
                .pattern("l l")
                .pattern("lll")
                .criterion("has_turtle_shell", conditionsFromItem(Items.TURTLE_HELMET))
                .offerTo(exporter, item(WizcraftItems.FOCUSES_BELT));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.WORKTABLE)
                .input('g', ConventionalItemTags.GOLD_INGOTS)
                .input('l', ConventionalItemTags.LAPIS_GEMS)
                .input('p', ItemTags.PLANKS)
                .input('s', ItemTags.WOODEN_SLABS)
                .pattern("glg")
                .pattern(" p ")
                .pattern("sss")
                .criterion("has_gold", conditionsFromTag(ConventionalItemTags.GOLD_INGOTS))
                .offerTo(exporter, block(WizcraftBlocks.DUMMY_WORKTABLE));
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.LENSING_PEDESTAL)
                .input('l', WizcraftItems.LENS)
                .input('b', ItemTags.STONE_BRICKS)
                .pattern("l")
                .pattern("b")
                .pattern("b")
                .criterion("has_lens", conditionsFromItem(WizcraftItems.LENS))
                .offerTo(exporter, block(WizcraftBlocks.LENSING_PEDESTAL));
    }

    private void generateFocusPlating(RecipeExporter exporter) {
        generateFocusPlating(exporter, FocusPlating.IRON, Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS));
        generateFocusPlating(exporter, FocusPlating.GOLD, Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS));
        generateFocusPlating(exporter, FocusPlating.COPPER, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS));
    }

    private void generateFocusPlating(RecipeExporter exporter, FocusPlating plating, Ingredient ingredient) {
        var platingComponents = ComponentChanges.builder().add(WizcraftComponents.FOCUS_PLATING, plating.index).build();
        for (var item : WizcraftItemTagProvider.FOCUSES) {
            var stack = new ItemStack(item);
            stack.applyChanges(platingComponents);
            new CustomSmithingTransformRecipeJsonBuilder(Ingredient.EMPTY, Ingredient.ofItems(item), ingredient, stack)
                    .offerTo(exporter, plating);
        }
    }

    private Identifier block(Block block) {
        return Registries.BLOCK.getId(block);
    }

    private Identifier item(Item item) {
        return Registries.ITEM.getId(item);
    }
}
