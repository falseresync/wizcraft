package falseresync.wizcraft.client.render;

import dev.emi.trinkets.api.client.*;
import falseresync.wizcraft.client.render.blockentity.*;
import falseresync.wizcraft.client.render.entity.*;
import falseresync.wizcraft.client.render.trinket.*;
import falseresync.wizcraft.client.render.world.*;
import falseresync.wizcraft.common.block.*;
import falseresync.wizcraft.common.blockentity.*;
import falseresync.wizcraft.common.data.component.*;
import falseresync.wizcraft.common.entity.*;
import falseresync.wizcraft.common.item.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.item.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.entity.*;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftRendering {
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

        ModelPredicateProviderRegistry.GLOBAL.put(wid("focus_plating"), (stack, world, entity, seed) -> stack.getOrDefault(WizcraftComponents.FOCUS_PLATING, -1));

        TrinketRendererRegistry.registerRenderer(WizcraftItems.TRUESEER_GOGGLES, new TrueseerGogglesRenderer());

        WorldRenderEvents.AFTER_ENTITIES.register(new CometWarpBeaconRenderer());
//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register();
    }
}
