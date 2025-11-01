package falseresync.wizcraft.client.render;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import falseresync.wizcraft.client.render.blockentity.ChargingWorktableRenderer;
import falseresync.wizcraft.client.render.blockentity.CraftingWorktableRenderer;
import falseresync.wizcraft.client.render.blockentity.CrucibleRenderer;
import falseresync.wizcraft.client.render.blockentity.LensRenderer;
import falseresync.wizcraft.client.render.blockentity.LensingPedestalRenderer;
import falseresync.wizcraft.client.render.entity.EnergyVeilFeatureRenderer;
import falseresync.wizcraft.client.render.entity.EnergyVeilModel;
import falseresync.wizcraft.client.render.entity.StarProjectileRenderer;
import falseresync.wizcraft.client.render.trinket.TrueseerGogglesRenderer;
import falseresync.wizcraft.client.render.world.CometWarpBeaconRenderer;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.blockentity.WizcraftBlockEntities;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.entity.WizcraftEntities;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftRendering {
    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(LensRenderer.LAYER, LensRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(EnergyVeilFeatureRenderer.LAYER, EnergyVeilModel::getTexturedModelData);

        EntityRendererRegistry.register(WizcraftEntities.STAR_PROJECTILE, StarProjectileRenderer::new);
        EntityRendererRegistry.register(WizcraftEntities.ENERGY_VEIL, NoopRenderer::new);

        BlockEntityRenderers.register(WizcraftBlockEntities.LENS, LensRenderer::new);
        BlockEntityRenderers.register(WizcraftBlockEntities.CRAFTING_WORKTABLE, CraftingWorktableRenderer::new);
        BlockEntityRenderers.register(WizcraftBlockEntities.CHARGING_WORKTABLE, ChargingWorktableRenderer::new);
        BlockEntityRenderers.register(WizcraftBlockEntities.LENSING_PEDESTAL, LensingPedestalRenderer::new);
        BlockEntityRenderers.register(WizcraftBlockEntities.CRUCIBLE, CrucibleRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderType.cutout(),
                WizcraftBlocks.CRUCIBLE,
                WizcraftBlocks.LENSING_PEDESTAL,
                WizcraftBlocks.DUMMY_WORKTABLE,
                WizcraftBlocks.CRAFTING_WORKTABLE,
                WizcraftBlocks.CHARGING_WORKTABLE);
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderType.translucent(),
                WizcraftBlocks.LENS);

        BuiltinItemRendererRegistry.INSTANCE.register(WizcraftItems.LENS, new LensRenderer.ItemRenderer());

        ItemProperties.GENERIC_PROPERTIES.put(wid("focus_plating"), (stack, world, entity, seed) -> stack.getOrDefault(WizcraftComponents.FOCUS_PLATING, -1));

        TrinketRendererRegistry.registerRenderer(WizcraftItems.TRUESEER_GOGGLES, new TrueseerGogglesRenderer());

        var cometWarpBeaconRenderer = new CometWarpBeaconRenderer();
        WorldRenderEvents.AFTER_ENTITIES.register(cometWarpBeaconRenderer);
        WorldRenderEvents.END.register(cometWarpBeaconRenderer);
//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register();
    }
}
