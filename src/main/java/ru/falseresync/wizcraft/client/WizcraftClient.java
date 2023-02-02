package ru.falseresync.wizcraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import ru.falseresync.wizcraft.client.render.block.entity.MagicCauldronRenderer;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;
import ru.falseresync.wizcraft.common.init.WizBlocks;

@Environment(EnvType.CLIENT)
public class WizcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getCutout(),
                WizBlocks.MAGIC_CAULDRON
        );
        BlockEntityRendererFactories.register(WizBlockEntities.MAGIC_CAULDRON, MagicCauldronRenderer::new);
    }
}
