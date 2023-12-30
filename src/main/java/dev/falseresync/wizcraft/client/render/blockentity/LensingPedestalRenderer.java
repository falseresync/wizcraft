package dev.falseresync.wizcraft.client.render.blockentity;

import dev.falseresync.wizcraft.common.block.entity.LensingPedestalBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class LensingPedestalRenderer implements BlockEntityRenderer<LensingPedestalBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public LensingPedestalRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(LensingPedestalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var slot = entity.storage.getSlot(0);
        var world = entity.getWorld();
        if (slot.isResourceBlank() || world == null) {
            return;
        }

        matrices.push();

        var offset = MathHelper.sin((world.getTime() + tickDelta) / 16) / 16;
        matrices.translate(0.5, 1.25 + offset, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() + tickDelta));
        matrices.scale(0.75f, 0.75f, 0.75f);

        var lightAbove = WorldRenderer.getLightmapCoordinates(world, entity.getPos().up());
        var stack = slot.getResource().toStack();
        this.itemRenderer.renderItem(
                stack,
                ModelTransformationMode.FIXED,
                lightAbove,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                world,
                0);

        matrices.pop();
    }
}
