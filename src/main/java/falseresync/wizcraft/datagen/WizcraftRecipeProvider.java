package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.datagen.recipe.LensedWorktableRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class WizcraftRecipeProvider extends FabricRecipeProvider {
    public WizcraftRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.STARSHOOTER_FOCUS, Ingredient.ofItems(Items.COPPER_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.GOLD_INGOT))
                .pedestalInput(Ingredient.ofItems(Items.FIRE_CHARGE))
                .offerTo(exporter, lwPrefix(item(WizcraftItems.STARSHOOTER_FOCUS)));

        new LensedWorktableRecipeJsonBuilder(WizcraftItems.LIGHTNING_FOCUS, Ingredient.ofItems(Items.DIAMOND_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.LIGHTNING_ROD))
                .pedestalInput(Ingredient.ofItems(Items.IRON_INGOT))
                .offerTo(exporter, lwPrefix(item(WizcraftItems.LIGHTNING_FOCUS)));

        new LensedWorktableRecipeJsonBuilder(WizcraftItems.COMET_WARP_FOCUS, Ingredient.ofItems(Items.SLIME_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.ENDER_PEARL))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .pedestalInput(Ingredient.ofItems(Items.ENDER_PEARL))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .offerTo(exporter, lwPrefix(item(WizcraftItems.COMET_WARP_FOCUS)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.MORTAR_AND_PESTLE)
                .input('i', Items.IRON_NUGGET)
                .input('f', Items.FLINT)
                .input('s', Items.SMOOTH_STONE_SLAB)
                .pattern("i")
                .pattern("f")
                .pattern("s")
                .criterion("unlock_right_away", TickCriterion.Conditions.createTick())
                .offerTo(exporter, item(WizcraftItems.MORTAR_AND_PESTLE));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.METALLIZED_STICK)
                .input(Items.STICK)
                .input(Items.COPPER_INGOT)
                .criterion("has_copper", conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter, item(WizcraftItems.METALLIZED_STICK));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.WAND_CORE)
                .input(Items.DIAMOND)
                .input(Items.AMETHYST_SHARD)
                .input(Items.REDSTONE)
                .input(Ingredient.ofItems(Items.SLIME_BALL, Items.HONEY_BOTTLE))
                .input(WizcraftItems.MORTAR_AND_PESTLE)
                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                .criterion("has_amethyst", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, item(WizcraftItems.WAND_CORE));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.WAND)
                .input('w', WizcraftItems.WAND_CORE)
                .input('g', Items.GOLD_INGOT)
                .input('s', WizcraftItems.METALLIZED_STICK)
                .pattern("  w")
                .pattern(" g ")
                .pattern("s  ")
                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                .criterion("has_amethyst", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, item(WizcraftItems.WAND));

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, WizcraftItems.LENS)
                .input('g', Items.GOLD_INGOT)
                .input('d', Items.DIAMOND)
                .input('a', Items.AMETHYST_SHARD)
                .pattern("aaa")
                .pattern("gdg")
                .pattern("aaa")
                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                .criterion("has_amethyst", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, block(WizcraftBlocks.LENS));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.WORKTABLE)
                .input('g', Items.GOLD_INGOT)
                .input('l', Items.LAPIS_LAZULI)
                .input('p', ItemTags.PLANKS)
                .input('s', ItemTags.WOODEN_SLABS)
                .pattern("glg")
                .pattern(" p ")
                .pattern("sss")
                .criterion("has_gold", conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, block(WizcraftBlocks.DUMMY_WORKTABLE));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.LENSING_PEDESTAL)
                .input('l', WizcraftItems.LENS)
                .input('b', Items.STONE_BRICKS)
                .pattern("l")
                .pattern("b")
                .pattern("b")
                .criterion("has_lens", conditionsFromItem(WizcraftItems.LENS))
                .offerTo(exporter, block(WizcraftBlocks.LENSING_PEDESTAL));
    }

    private Identifier lwPrefix(Identifier id) {
        return id.withPrefixedPath("lensed_worktable/");
    }

    private Identifier block(Block block) {
        return Registries.BLOCK.getId(block);
    }

    private Identifier item(Item item) {
        return Registries.ITEM.getId(item);
    }
}
