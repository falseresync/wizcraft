package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.item.focus.FocusPlating;
import falseresync.wizcraft.datagen.recipe.CustomSmithingTransformRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class WizcraftVanillaRecipeProvider extends FabricRecipeProvider {
    public WizcraftVanillaRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Vanilla Recipes";
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateCrafting(exporter);
        generateFocusPlating(exporter);
    }

    private void generateCrafting(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, WizcraftItems.MORTAR_AND_PESTLE)
                .define('i', ConventionalItemTags.IRON_NUGGETS)
                .define('f', Items.FLINT)
                .define('s', Items.SMOOTH_STONE_SLAB)
                .pattern("i")
                .pattern("f")
                .pattern("s")
                .unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
                .save(exporter, item(WizcraftItems.MORTAR_AND_PESTLE));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, WizcraftItems.GRIMOIRE)
                .requires(ConventionalItemTags.DIAMOND_GEMS)
                .requires(ConventionalItemTags.AMETHYST_GEMS)
                .requires(ConventionalItemTags.REDSTONE_DUSTS)
                .requires(Items.BOOK)
                .requires(WizcraftItems.MORTAR_AND_PESTLE)
                .unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
                .save(exporter, item(WizcraftItems.GRIMOIRE));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, WizcraftItems.WAND_CORE)
                .requires(ConventionalItemTags.DIAMOND_GEMS)
                .requires(ConventionalItemTags.AMETHYST_GEMS)
                .requires(ConventionalItemTags.REDSTONE_DUSTS)
                .requires(Ingredient.of(Items.SLIME_BALL, Items.HONEY_BOTTLE))
                .requires(WizcraftItems.MORTAR_AND_PESTLE)
                .unlockedBy("has_diamond", has(ConventionalItemTags.DIAMOND_GEMS))
                .unlockedBy("has_amethyst", has(ConventionalItemTags.AMETHYST_GEMS))
                .save(exporter, item(WizcraftItems.WAND_CORE));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, WizcraftItems.WAND)
                .define('w', WizcraftItems.WAND_CORE)
                .define('g', ConventionalItemTags.GOLD_INGOTS)
                .define('s', WizcraftItems.METALLIZED_STICK)
                .pattern("  w")
                .pattern(" g ")
                .pattern("s  ")
                .unlockedBy("has_diamond", has(ConventionalItemTags.DIAMOND_GEMS))
                .unlockedBy("has_amethyst", has(ConventionalItemTags.AMETHYST_GEMS))
                .save(exporter, item(WizcraftItems.WAND));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, WizcraftItems.TRUESEER_GOGGLES)
                .define('g', ConventionalItemTags.GOLD_NUGGETS)
                .define('h', Items.CHAINMAIL_HELMET)
                .define('p', Items.PHANTOM_MEMBRANE)
                .define('c', ConventionalItemTags.PRISMARINE_GEMS)
                .pattern("g g")
                .pattern("php")
                .pattern("c c")
                .unlockedBy("has_phantom_membrane", has(Items.PHANTOM_MEMBRANE))
                .unlockedBy("has_prismarine", has(ConventionalItemTags.PRISMARINE_GEMS))
                .save(exporter, item(WizcraftItems.TRUESEER_GOGGLES));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, WizcraftItems.FOCUSES_BELT)
                .define('l', ConventionalItemTags.LEATHERS)
                .define('t', Items.TURTLE_HELMET)
                .pattern("ltl")
                .pattern("l l")
                .pattern("lll")
                .unlockedBy("has_turtle_shell", has(Items.TURTLE_HELMET))
                .save(exporter, item(WizcraftItems.FOCUSES_BELT));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.WORKTABLE)
                .define('g', ConventionalItemTags.GOLD_INGOTS)
                .define('l', ConventionalItemTags.LAPIS_GEMS)
                .define('p', ItemTags.PLANKS)
                .define('s', ItemTags.WOODEN_SLABS)
                .pattern("glg")
                .pattern(" p ")
                .pattern("sss")
                .unlockedBy("has_gold", has(ConventionalItemTags.GOLD_INGOTS))
                .save(exporter, block(WizcraftBlocks.DUMMY_WORKTABLE));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.LENSING_PEDESTAL)
                .define('l', WizcraftItems.LENS)
                .define('b', ItemTags.STONE_BRICKS)
                .pattern("l")
                .pattern("b")
                .pattern("b")
                .unlockedBy("has_lens", has(WizcraftItems.LENS))
                .save(exporter, block(WizcraftBlocks.LENSING_PEDESTAL));
    }

    private void generateFocusPlating(RecipeOutput exporter) {
        generateFocusPlating(exporter, FocusPlating.IRON, Ingredient.of(ConventionalItemTags.IRON_INGOTS));
        generateFocusPlating(exporter, FocusPlating.GOLD, Ingredient.of(ConventionalItemTags.GOLD_INGOTS));
        generateFocusPlating(exporter, FocusPlating.COPPER, Ingredient.of(ConventionalItemTags.COPPER_INGOTS));
    }

    private void generateFocusPlating(RecipeOutput exporter, FocusPlating plating, Ingredient ingredient) {
        var platingComponents = DataComponentPatch.builder().set(WizcraftComponents.FOCUS_PLATING, plating.index).build();
        for (var item : WizcraftItemTagProvider.FOCUSES) {
            var stack = new ItemStack(item);
            stack.applyComponentsAndValidate(platingComponents);
            new CustomSmithingTransformRecipeJsonBuilder(Ingredient.EMPTY, Ingredient.of(item), ingredient, stack)
                    .offerTo(exporter, plating);
        }
    }

    private ResourceLocation block(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private ResourceLocation item(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
