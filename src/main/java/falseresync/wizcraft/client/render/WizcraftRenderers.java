package falseresync.wizcraft.client.render;

import falseresync.wizcraft.client.render.blockentity.ChargingWorktableRenderer;
import falseresync.wizcraft.client.render.blockentity.CraftingWorktableRenderer;
import falseresync.wizcraft.client.render.blockentity.LensingPedestalRenderer;
import falseresync.wizcraft.client.render.entity.StarProjectileRenderer;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.blockentity.WizcraftBlockEntities;
import falseresync.wizcraft.common.entity.WizcraftEntities;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class WizcraftRenderers {
    public static void init() {
        EntityRendererRegistry.register(WizcraftEntities.STAR_PROJECTILE, StarProjectileRenderer::new);

        BlockEntityRendererFactories.register(WizcraftBlockEntities.CRAFTING_WORKTABLE, CraftingWorktableRenderer::new);
        BlockEntityRendererFactories.register(WizcraftBlockEntities.CHARGING_WORKTABLE, ChargingWorktableRenderer::new);
        BlockEntityRendererFactories.register(WizcraftBlockEntities.LENSING_PEDESTAL, LensingPedestalRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                WizcraftBlocks.LENSING_PEDESTAL,
                WizcraftBlocks.DUMMY_WORKTABLE,
                WizcraftBlocks.CRAFTING_WORKTABLE,
                WizcraftBlocks.CHARGING_WORKTABLE);
    }
}
