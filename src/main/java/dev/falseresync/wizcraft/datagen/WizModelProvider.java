package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.item.WizItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class WizModelProvider extends FabricModelProvider {
    public WizModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(WizItems.SKY_WAND, Models.GENERATED);
        itemModelGenerator.register(WizItems.CHARGING_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizItems.STARSHOOTER_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizItems.LIGHTNING_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizItems.COMET_WARP_FOCUS, Models.GENERATED);
    }
}
