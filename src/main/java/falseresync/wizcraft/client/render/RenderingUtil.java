package falseresync.wizcraft.client.render;

import com.mojang.blaze3d.systems.*;
import falseresync.lib.math.*;
import falseresync.wizcraft.common.*;
import net.fabricmc.fabric.api.client.render.fluid.v1.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.particle.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

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

        switch (Wizcraft.getConfig().animationQuality) {
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

    public static void drawFluidOnBlockEntity(MatrixStack matrices, VertexConsumer buffer, BlockRenderView view, BlockPos pos, Fluid fluid, FluidState state, boolean still, int light, int overlay, float x, float width, float y, float height, float depth) {
        var handler = Objects.requireNonNull(FluidRenderHandlerRegistry.INSTANCE.get(fluid));
        var sprites = handler.getFluidSprites(view, pos, state);
        var tint = handler.getFluidColor(view, pos, state);
        RenderingUtil.drawSpriteOnBlockEntity(matrices, buffer, still ? sprites[0] : sprites[1], Color.ofRgb(tint).argb(), light, overlay, x, width, y, height, depth);
    }

    public static void drawSpriteOnBlockEntity(MatrixStack matrices, VertexConsumer buffer, Sprite sprite, int tint, int light, int overlay, float x, float width, float y, float height, float depth) {
        drawTextureOnBlockEntity(matrices, buffer, sprite.getAtlasId(), tint, light, overlay,
                x, x + width, y, y + height, depth,
                sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public static void drawTextureOnBlockEntity(MatrixStack matrices, VertexConsumer buffer, Identifier texture, int tint, int light, int overlay, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        var positionMatrix = matrices.peek().getPositionMatrix();
        buffer.vertex(positionMatrix, x1, y1, z).texture(u1, v1).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
        buffer.vertex(positionMatrix, x1, y2, z).texture(u1, v2).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
        buffer.vertex(positionMatrix, x2, y2, z).texture(u2, v2).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
        buffer.vertex(positionMatrix, x2, y1, z).texture(u2, v1).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
    }
}
