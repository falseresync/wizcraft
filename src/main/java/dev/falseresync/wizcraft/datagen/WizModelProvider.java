package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.item.WizItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class WizModelProvider extends FabricModelProvider {
    public static final TextureKey KEY_OVERLAY = TextureKey.of("overlay");
    public static final Model MODEL_TEMPLATE_WORKTABLE = new Model(
            Optional.of(new Identifier(Wizcraft.MODID, "block/template_worktable")), Optional.empty(), KEY_OVERLAY);
    public static final TexturedModel.Factory WORKTABLE = TexturedModel.makeFactory(
            block -> new TextureMap().put(KEY_OVERLAY, TextureMap.getSubId(block, "_overlay")),
            MODEL_TEMPLATE_WORKTABLE);

    public WizModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        var dummyWorktableModelId = new Identifier(Wizcraft.MODID, "block/dummy_worktable");
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(
                WizBlocks.DUMMY_WORKTABLE,
                BlockStateVariant.create().put(VariantSettings.MODEL, dummyWorktableModelId)));
        blockStateModelGenerator.registerParentedItemModel(WizBlocks.DUMMY_WORKTABLE, dummyWorktableModelId);

        createWorktableVariant(blockStateModelGenerator, WizBlocks.CRAFTING_WORKTABLE);
        createWorktableVariant(blockStateModelGenerator, WizBlocks.CHARGING_WORKTABLE);

        var lensingPedestalModelId = ModelIds.getBlockModelId(WizBlocks.LENSING_PEDESTAL);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        WizBlocks.LENSING_PEDESTAL,
                        BlockStateVariant.create().put(VariantSettings.MODEL, lensingPedestalModelId)));
        blockStateModelGenerator.registerParentedItemModel(WizBlocks.LENSING_PEDESTAL, lensingPedestalModelId);
    }

    private void createWorktableVariant(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        block,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                WORKTABLE.upload(block, blockStateModelGenerator.modelCollector))));
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
