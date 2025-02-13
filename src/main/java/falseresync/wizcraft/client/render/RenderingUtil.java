package falseresync.wizcraft.client.render;

import falseresync.wizcraft.common.WizcraftConfig;
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
import org.jetbrains.annotations.Nullable;

public class RenderingUtil {
    public static final Vec3d UNIT_VEC3D = RenderingUtil.getSymmetricVec3d(1);

    public static Vec3d getSymmetricVec3d(double value) {
        return new Vec3d(value, value, value);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        levitateItemAboveBlock(world, pos, Vec3d.ZERO, RenderingUtil.UNIT_VEC3D, tickDelta, stack, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, float tickDelta, ItemStack stack, ModelTransformationMode mode, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        levitateItemAboveBlock(world, pos, Vec3d.ZERO, RenderingUtil.UNIT_VEC3D, tickDelta, stack, mode, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, Vec3d translation, Vec3d scale, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        levitateItemAboveBlock(world, pos, translation, scale, tickDelta, stack, ModelTransformationMode.FIXED, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, Vec3d translation, Vec3d scale, float tickDelta, ItemStack stack, ModelTransformationMode mode, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (stack.isEmpty()) return;

        switch (WizcraftConfig.animationQuality) {
            case DEFAULT -> {
                matrices.push();

                var offset = MathHelper.sin((world.getTime() + tickDelta) / 16) / 16;
                matrices.translate(0.5 + translation.x, 1.25 + offset + translation.y, 0.5 + translation.z);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() + tickDelta));

                scale = scale.multiply(stack.getItem() instanceof BlockItem ? 0.75 : 0.5);
                matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);

                var lightAbove = WorldRenderer.getLightmapCoordinates(world, pos.up());
                itemRenderer.renderItem(stack, mode, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);

                matrices.pop();
            }
            case FAST -> {
                matrices.push();
                var lightAbove = WorldRenderer.getLightmapCoordinates(world, pos.up());
                itemRenderer.renderItem(stack, mode, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);
                matrices.pop();
            }
        }
    }

    public static void addParticle(World world, @Nullable ParticleEffect parameters, Vec3d position, Vec3d velocity) {
        if (parameters != null) {
            world.addParticle(parameters, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        }
    }
}
