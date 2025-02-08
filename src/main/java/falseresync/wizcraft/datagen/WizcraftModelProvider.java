package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;

import java.util.Optional;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftModelProvider extends FabricModelProvider {
    public static final TextureKey KEY_OVERLAY = TextureKey.of("overlay");
    public static final Model MODEL_TEMPLATE_WORKTABLE = new Model(Optional.of(wid("block/template_worktable")), Optional.empty(), KEY_OVERLAY);
    public static final TexturedModel.Factory WORKTABLE = TexturedModel.makeFactory(block -> new TextureMap().put(KEY_OVERLAY, TextureMap.getSubId(block, "_overlay")), MODEL_TEMPLATE_WORKTABLE);

    public WizcraftModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        addSimpleBlock(blockStateModelGenerator, WizcraftBlocks.CRUCIBLE);

        addSimpleBlockWithoutItem(blockStateModelGenerator, WizcraftBlocks.LENS);
        addSimpleBlock(blockStateModelGenerator, WizcraftBlocks.LENSING_PEDESTAL);

        addSimpleBlock(blockStateModelGenerator, WizcraftBlocks.DUMMY_WORKTABLE);
        addWorktableVariant(blockStateModelGenerator, WizcraftBlocks.CRAFTING_WORKTABLE);
        addWorktableVariant(blockStateModelGenerator, WizcraftBlocks.CHARGING_WORKTABLE);
    }

    private static void addWorktableVariant(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        block,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                WORKTABLE.upload(block, blockStateModelGenerator.modelCollector))));
    }

    private static void addSimpleBlockWithoutItem(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        var blockModelId = ModelIds.getBlockModelId(block);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        block,
                        BlockStateVariant.create().put(VariantSettings.MODEL, blockModelId)));
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(block);
    }

    private static void addSimpleBlock(BlockStateModelGenerator blockStateModelGenerator, Block block) {
        var blockModelId = ModelIds.getBlockModelId(block);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        block,
                        BlockStateVariant.create().put(VariantSettings.MODEL, blockModelId)));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(WizcraftItems.GRIMOIRE, Models.GENERATED);

        itemModelGenerator.register(WizcraftItems.MORTAR_AND_PESTLE, Models.GENERATED);

        itemModelGenerator.register(WizcraftItems.WAND_CORE, Models.GENERATED);

        itemModelGenerator.register(WizcraftItems.WAND, Models.HANDHELD_ROD);

        itemModelGenerator.register(WizcraftItems.CHARGING_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.STARSHOOTER_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.LIGHTNING_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.COMET_WARP_FOCUS, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.ENERGY_VEIL_FOCUS, Models.GENERATED);

        itemModelGenerator.register(WizcraftItems.TRUESEER_GOGGLES, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.FOCUSES_BELT, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.CHARGE_SHELL, Models.GENERATED);
    }
}
