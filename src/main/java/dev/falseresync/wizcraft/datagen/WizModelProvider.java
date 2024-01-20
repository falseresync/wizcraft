package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.item.WizItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class WizModelProvider extends FabricModelProvider {
    public WizModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        WizBlocks.DUMMY_WORKTABLE,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                TexturedModel.CUBE_BOTTOM_TOP.upload(
                                        WizBlocks.DUMMY_WORKTABLE,
                                        blockStateModelGenerator.modelCollector))));
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        WizBlocks.CRAFTING_WORKTABLE,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                TexturedModel.CUBE_BOTTOM_TOP.upload(
                                        WizBlocks.CRAFTING_WORKTABLE,
                                        blockStateModelGenerator.modelCollector))));
        var lensingPedestalModelId = ModelIds.getBlockModelId(WizBlocks.LENSING_PEDESTAL);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        WizBlocks.LENSING_PEDESTAL,
                        BlockStateVariant.create().put(VariantSettings.MODEL, lensingPedestalModelId)));
        blockStateModelGenerator.registerParentedItemModel(WizBlocks.LENSING_PEDESTAL, lensingPedestalModelId);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(WizItems.WAND, Models.GENERATED);
        itemModelGenerator.register(WizItems.CHARGING_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizItems.STARSHOOTER_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizItems.LIGHTNING_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizItems.COMET_WARP_FOCUS, Models.GENERATED);
    }
}
