package ru.falseresync.wizcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import ru.falseresync.wizcraft.client.render.block.entity.MagicCauldronBlockEntityRenderer;
import ru.falseresync.wizcraft.block.entity.WizBlockEntities;
import ru.falseresync.wizcraft.block.WizBlocks;

@Environment(EnvType.CLIENT)
public class WizcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getCutout(),
                WizBlocks.MAGIC_CAULDRON
        );
        BlockEntityRendererRegistry.register(WizBlockEntities.MAGIC_CAULDRON, MagicCauldronBlockEntityRenderer::new);
    }
}
