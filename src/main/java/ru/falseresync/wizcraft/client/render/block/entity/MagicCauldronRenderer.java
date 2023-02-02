package ru.falseresync.wizcraft.client.render.block.entity;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import ru.falseresync.wizcraft.common.block.entity.MagicCauldronBlockEntity;
import ru.falseresync.wizcraft.lib.client.Color;

public class MagicCauldronRenderer implements BlockEntityRenderer<MagicCauldronBlockEntity> {
    public MagicCauldronRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(MagicCauldronBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var fluid = entity.fluidStorage.variant;
        if (fluid.isBlank()) {
            return;
        }

        matrices.push();

        var sprite = FluidVariantRendering.getSprite(fluid);
        var color = Color.fromArgb(FluidVariantRendering.getColor(fluid));
        var emitter = RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter();

        emitter.square(Direction.UP, 2 / 16f, 2 / 16f, 14 / 16f, 14 / 16f, 2 / 16f);
        // BAKE_LOCK_UV -> use the whole texture
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        // Enable texture usage (somehow)
        emitter.spriteColor(0, -1, -1, -1, -1);

        vertexConsumers
                .getBuffer(RenderLayer.getSolid())
                .quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), color.r(), color.g(), color.b(), LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, overlay);

        matrices.pop();
    }
}
