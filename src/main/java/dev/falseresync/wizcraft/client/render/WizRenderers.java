package dev.falseresync.wizcraft.client.render;

import dev.falseresync.wizcraft.client.render.blockentity.EnergizedWorktableRenderer;
import dev.falseresync.wizcraft.client.render.blockentity.LensingPedestalRenderer;
import dev.falseresync.wizcraft.client.render.entity.StarProjectileRenderer;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.entity.WizEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class WizRenderers {
    static {
        EntityRendererRegistry.register(WizEntities.STAR_PROJECTILE, StarProjectileRenderer::new);

        BlockEntityRendererFactories.register(WizBlockEntities.ENERGIZED_WORKTABLE, EnergizedWorktableRenderer::new);
        BlockEntityRendererFactories.register(WizBlockEntities.LENSING_PEDESTAL, LensingPedestalRenderer::new);
    }

    public static void register() {}
}
