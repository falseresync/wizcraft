package dev.falseresync.wizcraft.client.render;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class CommonRenders {
    public static void levitateItemAboveBlock(World world, BlockPos pos, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var offset = MathHelper.sin((world.getTime() + tickDelta) / 16) / 16;
        matrices.translate(0.5, 1.25 + offset, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() + tickDelta));
        matrices.scale(0.75f, 0.75f, 0.75f);

        var lightAbove = WorldRenderer.getLightmapCoordinates(world, pos.up());
        itemRenderer.renderItem(
                stack,
                ModelTransformationMode.FIXED,
                lightAbove,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                world,
                0);
    }
}
