package dev.falseresync.wizcraft.client.render;

import dev.falseresync.wizcraft.client.render.blockentity.WorktableRenderer;
import dev.falseresync.wizcraft.client.render.blockentity.LensingPedestalRenderer;
import dev.falseresync.wizcraft.client.render.entity.StarProjectileRenderer;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.entity.WizEntities;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class WizRenderers {
    static {
        EntityRendererRegistry.register(WizEntities.STAR_PROJECTILE, StarProjectileRenderer::new);

        BlockEntityRendererFactories.register(WizBlockEntities.WORKTABLE, WorktableRenderer::new);
        BlockEntityRendererFactories.register(WizBlockEntities.LENSING_PEDESTAL, LensingPedestalRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(WizBlocks.LENSING_PEDESTAL, RenderLayer.getCutout());
    }

    public static void register() {}
}
