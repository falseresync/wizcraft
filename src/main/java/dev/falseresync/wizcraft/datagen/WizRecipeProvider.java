package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.block.WizcraftBlocks;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.datagen.recipe.LensedWorktableRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class WizRecipeProvider extends FabricRecipeProvider {
    public WizRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        new LensedWorktableRecipeJsonBuilder(WizcraftItems.STARSHOOTER_FOCUS, Ingredient.ofItems(Items.COPPER_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.GOLD_INGOT))
                .pedestalInput(Ingredient.ofItems(Items.FIRE_CHARGE))
                .offerTo(exporter, lwPrefix(WizcraftItems.STARSHOOTER_FOCUS));

        new LensedWorktableRecipeJsonBuilder(WizcraftItems.LIGHTNING_FOCUS, Ingredient.ofItems(Items.DIAMOND_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.LIGHTNING_ROD))
                .pedestalInput(Ingredient.ofItems(Items.IRON_INGOT))
                .offerTo(exporter, lwPrefix(WizcraftItems.LIGHTNING_FOCUS));

        new LensedWorktableRecipeJsonBuilder(WizcraftItems.COMET_WARP_FOCUS, Ingredient.ofItems(Items.SLIME_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.ENDER_PEARL))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .pedestalInput(Ingredient.ofItems(Items.ENDER_PEARL))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .offerTo(exporter, lwPrefix(WizcraftItems.COMET_WARP_FOCUS));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, WizcraftItems.WAND)
                .input('d', Items.DIAMOND)
                .input('g', Items.GOLD_INGOT)
                .input('s', Items.STICK)
                .pattern(" sd")
                .pattern(" gs")
                .pattern("s  ")
                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, WizcraftItems.WAND.getId());

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, WizcraftItems.LENS)
                .input('g', Items.GOLD_INGOT)
                .input('d', Items.DIAMOND)
                .input('a', Items.AMETHYST_SHARD)
                .pattern("aaa")
                .pattern("gdg")
                .pattern("aaa")
                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                .criterion("has_amethyst", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, WizcraftBlocks.LENS.getId());

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.WORKTABLE)
                .input('g', Items.GOLD_INGOT)
                .input('l', Items.LAPIS_LAZULI)
                .input('p', ItemTags.PLANKS)
                .input('s', ItemTags.WOODEN_SLABS)
                .pattern("glg")
                .pattern(" p ")
                .pattern("sss")
                .criterion("has_gold", conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, WizcraftBlocks.DUMMY_WORKTABLE.getId());

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizcraftItems.LENSING_PEDESTAL)
                .input('l', WizcraftItems.LENS)
                .input('b', Items.STONE_BRICKS)
                .pattern("l")
                .pattern("b")
                .pattern("b")
                .criterion("has_lens", conditionsFromItem(WizcraftItems.LENS))
                .offerTo(exporter, WizcraftBlocks.LENSING_PEDESTAL.getId());
    }

    private Identifier lwPrefix(HasId hasId) {
        return hasId.getId().withPrefixedPath("lensed_worktable/");
    }
}
