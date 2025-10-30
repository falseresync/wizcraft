package falseresync.wizcraft.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.item.focus.FocusItem;
import falseresync.wizcraft.common.item.focus.FocusPlating;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Optional;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftModelProvider extends FabricModelProvider {
    public static final TextureSlot KEY_OVERLAY = TextureSlot.create("overlay");
    public static final ModelTemplate MODEL_TEMPLATE_WORKTABLE =
            new ModelTemplate(Optional.of(wid("block/template_worktable")), Optional.empty(), KEY_OVERLAY);
    public static final TexturedModel.Provider WORKTABLE =
            TexturedModel.createDefault(block -> new TextureMapping().put(KEY_OVERLAY, TextureMapping.getBlockTexture(block, "_overlay")), MODEL_TEMPLATE_WORKTABLE);

    public WizcraftModelProvider(FabricDataOutput output) {
        super(output);
    }

    private static void addWorktableVariant(BlockModelGenerators blockStateModelGenerator, Block block) {
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        block,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                WORKTABLE.create(block, blockStateModelGenerator.modelOutput))));
    }

    private static void addSimpleBlockWithoutItem(BlockModelGenerators blockStateModelGenerator, Block block) {
        var blockModelId = ModelLocationUtils.getModelLocation(block);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        block,
                        Variant.variant().with(VariantProperties.MODEL, blockModelId)));
        blockStateModelGenerator.skipAutoItemBlock(block);
    }

    private static void addSimpleBlock(BlockModelGenerators blockStateModelGenerator, Block block) {
        var blockModelId = ModelLocationUtils.getModelLocation(block);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        block,
                        Variant.variant().with(VariantProperties.MODEL, blockModelId)));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        addSimpleBlock(blockStateModelGenerator, WizcraftBlocks.CRUCIBLE);

        addSimpleBlockWithoutItem(blockStateModelGenerator, WizcraftBlocks.LENS);
        addSimpleBlock(blockStateModelGenerator, WizcraftBlocks.LENSING_PEDESTAL);

        addSimpleBlock(blockStateModelGenerator, WizcraftBlocks.DUMMY_WORKTABLE);
        addWorktableVariant(blockStateModelGenerator, WizcraftBlocks.CRAFTING_WORKTABLE);
        addWorktableVariant(blockStateModelGenerator, WizcraftBlocks.CHARGING_WORKTABLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(WizcraftItems.GRIMOIRE, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(WizcraftItems.MORTAR_AND_PESTLE, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(WizcraftItems.WAND_CORE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(WizcraftItems.METALLIZED_STICK, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(WizcraftItems.WAND, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);

        registerFocus(WizcraftItems.CHARGING_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.STARSHOOTER_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.LIGHTNING_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.COMET_WARP_FOCUS, itemModelGenerator);
        registerFocus(WizcraftItems.ENERGY_VEIL_FOCUS, itemModelGenerator);

        itemModelGenerator.generateFlatItem(WizcraftItems.TRUESEER_GOGGLES, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(WizcraftItems.FOCUSES_BELT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(WizcraftItems.CHARGE_SHELL, ModelTemplates.FLAT_ITEM);
    }

    private JsonObject createFocusJson(ResourceLocation id, Map<TextureSlot, ResourceLocation> textures) {
        var model = ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(id, textures);
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

    private void registerFocus(FocusItem focus, ItemModelGenerators generator) {
        ResourceLocation modelId = ModelLocationUtils.getModelLocation(focus);
        ResourceLocation textureId = TextureMapping.getItemTexture(focus);
        ModelTemplates.FLAT_ITEM.create(modelId, TextureMapping.layer0(textureId), generator.output, this::createFocusJson);

        for (var plating : FocusPlating.values()) {
            ModelTemplates.TWO_LAYERED_ITEM.create(
                    DatagenUtil.suffixPlating(modelId, plating),
                    TextureMapping.layered(textureId, DatagenUtil.suffixPlating(wid("item/focus"), plating)),
                    generator.output);
        }
    }
}
