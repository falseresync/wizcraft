package dev.falseresync.wizcraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CommonRenders {
    public static void levitateItemAboveBlock(World world, BlockPos pos, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var offset = MathHelper.sin((world.getTime() + tickDelta) / 16) / 16;
        matrices.translate(0.5, 1.25 + offset, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() + tickDelta));

        if (stack.getItem() instanceof BlockItem) {
            matrices.scale(0.75f, 0.75f, 0.75f);
        } else {
            matrices.scale(0.5f, 0.5f, 0.5f);
        }

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

    public static void addParticle(World world, ParticleEffect parameters, Vec3d position, Vec3d velocity) {
        world.addParticle(parameters, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }
}
