package dev.falseresync.wizcraft.client.render;

import dev.falseresync.wizcraft.client.render.entity.StarProjectileRenderer;
import dev.falseresync.wizcraft.common.entity.WizEntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class WizRenderers {
    static {
        EntityRendererRegistry.register(WizEntityTypes.STAR_PROJECTILE, StarProjectileRenderer::new);
    }

    public static void register() {}
}
