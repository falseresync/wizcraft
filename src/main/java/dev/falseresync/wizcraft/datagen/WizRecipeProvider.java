package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
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
    }
}
