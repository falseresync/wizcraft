package falseresync.wizcraft.client.render;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import falseresync.wizcraft.client.render.blockentity.*;
import falseresync.wizcraft.client.render.entity.EnergyVeilFeatureRenderer;
import falseresync.wizcraft.client.render.entity.EnergyVeilModel;
import falseresync.wizcraft.client.render.entity.StarProjectileRenderer;
import falseresync.wizcraft.client.render.trinket.TrueseerGogglesRenderer;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.blockentity.WizcraftBlockEntities;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.entity.WizcraftEntities;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftRenderers {
    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(LensRenderer.LAYER, LensRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(EnergyVeilFeatureRenderer.LAYER, EnergyVeilModel::getTexturedModelData);

        EntityRendererRegistry.register(WizcraftEntities.STAR_PROJECTILE, StarProjectileRenderer::new);
        EntityRendererRegistry.register(WizcraftEntities.ENERGY_VEIL, EmptyEntityRenderer::new);

        BlockEntityRendererFactories.register(WizcraftBlockEntities.LENS, LensRenderer::new);
        BlockEntityRendererFactories.register(WizcraftBlockEntities.CRAFTING_WORKTABLE, CraftingWorktableRenderer::new);
        BlockEntityRendererFactories.register(WizcraftBlockEntities.CHARGING_WORKTABLE, ChargingWorktableRenderer::new);
        BlockEntityRendererFactories.register(WizcraftBlockEntities.LENSING_PEDESTAL, LensingPedestalRenderer::new);
        BlockEntityRendererFactories.register(WizcraftBlockEntities.CRUCIBLE, CrucibleRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                WizcraftBlocks.CRUCIBLE,
                WizcraftBlocks.LENSING_PEDESTAL,
                WizcraftBlocks.DUMMY_WORKTABLE,
                WizcraftBlocks.CRAFTING_WORKTABLE,
                WizcraftBlocks.CHARGING_WORKTABLE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
                WizcraftBlocks.LENS);

        BuiltinItemRendererRegistry.INSTANCE.register(WizcraftItems.LENS, new LensRenderer.ItemRenderer());

        ModelPredicateProviderRegistry.GLOBAL.put(wid("focus_plating"), (stack, world, entity, seed) -> stack.getOrDefault(WizcraftDataComponents.FOCUS_PLATING, -1));

        TrinketRendererRegistry.registerRenderer(WizcraftItems.TRUESEER_GOGGLES, new TrueseerGogglesRenderer());
    }
}
