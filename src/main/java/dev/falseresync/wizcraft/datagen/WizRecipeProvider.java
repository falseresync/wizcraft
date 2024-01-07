package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
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
        new LensedWorktableRecipeJsonBuilder(WizItems.STARSHOOTER_FOCUS, Ingredient.ofItems(Items.COPPER_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.GOLD_INGOT))
                .pedestalInput(Ingredient.ofItems(Items.FIRE_CHARGE))
                .offerTo(exporter, new Identifier(Wizcraft.MODID, "skywand/starshooter_focus"));

        new LensedWorktableRecipeJsonBuilder(WizItems.LIGHTNING_FOCUS, Ingredient.ofItems(Items.DIAMOND_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.LIGHTNING_ROD))
                .pedestalInput(Ingredient.ofItems(Items.IRON_INGOT))
                .offerTo(exporter, new Identifier(Wizcraft.MODID, "skywand/lightning_focus"));

        new LensedWorktableRecipeJsonBuilder(WizItems.COMET_WARP_FOCUS, Ingredient.ofItems(Items.SLIME_BLOCK))
                .pedestalInput(Ingredient.ofItems(Items.ENDER_PEARL))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .pedestalInput(Ingredient.ofItems(Items.ENDER_PEARL))
                .pedestalInput(Ingredient.ofItems(Items.CHORUS_FRUIT))
                .offerTo(exporter, new Identifier(Wizcraft.MODID, "skywand/comet_warp_focus"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizItems.SKY_WAND)
                .input('d', Items.DIAMOND)
                .input('g', Items.GOLD_INGOT)
                .input('s', Items.STICK)
                .pattern(" sd")
                .pattern(" gs")
                .pattern("s  ")
                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, new Identifier(Wizcraft.MODID, "sky_wand"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizItems.PLATED_WORKTABLE)
                .input('g', Items.GOLD_INGOT)
                .input('c', Items.CRAFTING_TABLE)
                .input('d', Items.DIAMOND)
                .input('s', ItemTags.WOODEN_SLABS)
                .pattern("gdg")
                .pattern("gcg")
                .pattern("sss")
                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, new Identifier(Wizcraft.MODID, "energized_worktable"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, WizItems.LENSING_PEDESTAL)
                .input('a', Items.AMETHYST_BLOCK)
                .input('b', Items.STONE_BRICKS)
                .pattern("a")
                .pattern("b")
                .pattern("b")
                .criterion("has_amethyst", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, new Identifier(Wizcraft.MODID, "lensing_pedestal"));
    }
}
