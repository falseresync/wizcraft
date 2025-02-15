package falseresync.wizcraft.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.item.focus.FocusItem;
import falseresync.wizcraft.common.item.focus.FocusPlating;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftModelProvider extends FabricModelProvider {
    public static final TextureKey KEY_OVERLAY = TextureKey.of("overlay");
    public static final Model MODEL_TEMPLATE_WORKTABLE =
            new Model(Optional.of(wid("block/template_worktable")), Optional.empty(), KEY_OVERLAY);
    public static final TexturedModel.Factory WORKTABLE =
            TexturedModel.makeFactory(block -> new TextureMap().put(KEY_OVERLAY, TextureMap.getSubId(block, "_overlay")), MODEL_TEMPLATE_WORKTABLE);

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
        itemModelGenerator.register(WizcraftItems.METALLIZED_STICK, Models.GENERATED);

        itemModelGenerator.register(WizcraftItems.WAND, Models.HANDHELD_ROD);

        registerFocus(WizcraftItems.CHARGING_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.STARSHOOTER_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.LIGHTNING_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.COMET_WARP_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.ENERGY_VEIL_FOCUS, itemModelGenerator);

        itemModelGenerator.register(WizcraftItems.TRUESEER_GOGGLES, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.FOCUSES_BELT, Models.GENERATED);
        itemModelGenerator.register(WizcraftItems.CHARGE_SHELL, Models.GENERATED);
    }

    private JsonObject createFocusJson(Identifier id, Map<TextureKey, Identifier> textures) {
        var model = Models.GENERATED_TWO_LAYERS.createJson(id, textures);
        var overrides = new JsonArray();

        for (var plating : FocusPlating.values()) {
            var override = new JsonObject();
            var predicate = new JsonObject();
            predicate.addProperty("wizcraft:focus_plating", plating.index);
            override.add("predicate", predicate);
            override.addProperty("model", DatagenUtil.suffixPlating(id, plating).toString());
            overrides.add(override);
        }

        model.add("overrides", overrides);
        return model;
    }

    private void registerFocus(FocusItem focus, ItemModelGenerator generator) {
        Identifier modelId = ModelIds.getItemModelId(focus);
        Identifier textureId = TextureMap.getId(focus);
        Models.GENERATED.upload(modelId, TextureMap.layer0(textureId), generator.writer, this::createFocusJson);

        for (var plating : FocusPlating.values()) {
            Models.GENERATED_TWO_LAYERS.upload(
                    DatagenUtil.suffixPlating(modelId, plating),
                    TextureMap.layered(textureId, DatagenUtil.suffixPlating(wid("item/focus"), plating)),
                    generator.writer);
        }
    }
}
