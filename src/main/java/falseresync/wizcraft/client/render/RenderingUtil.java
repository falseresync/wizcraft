package falseresync.wizcraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import falseresync.lib.math.Color;
import falseresync.wizcraft.common.Wizcraft;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RenderingUtil {
    public static final Vec3 UNIT_VEC3D = RenderingUtil.getSymmetricVec3d(1);

    public static Vec3 getSymmetricVec3d(double value) {
        return new Vec3(value, value, value);
    }

    public static void levitateItemAboveBlock(Level world, BlockPos pos, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, PoseStack matrices, MultiBufferSource vertexConsumers) {
        levitateItemAboveBlock(world, pos, Vec3.ZERO, RenderingUtil.UNIT_VEC3D, tickDelta, stack, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(Level world, BlockPos pos, float tickDelta, ItemStack stack, ItemDisplayContext mode, ItemRenderer itemRenderer, PoseStack matrices, MultiBufferSource vertexConsumers) {
        levitateItemAboveBlock(world, pos, Vec3.ZERO, RenderingUtil.UNIT_VEC3D, tickDelta, stack, mode, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(Level world, BlockPos pos, Vec3 translation, Vec3 scale, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, PoseStack matrices, MultiBufferSource vertexConsumers) {
        levitateItemAboveBlock(world, pos, translation, scale, tickDelta, stack, ItemDisplayContext.FIXED, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(Level world, BlockPos pos, Vec3 translation, Vec3 scale, float tickDelta, ItemStack stack, ItemDisplayContext mode, ItemRenderer itemRenderer, PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (stack.isEmpty()) return;

        switch (Wizcraft.getConfig().animationQuality) {
            case DEFAULT -> {
                matrices.pushPose();

                var offset = Mth.sin((world.getGameTime() + tickDelta) / 16) / 16;
                matrices.translate(0.5 + translation.x, 1.25 + offset + translation.y, 0.5 + translation.z);
                matrices.mulPose(Axis.YP.rotationDegrees(world.getGameTime() + tickDelta));

                scale = scale.scale(stack.getItem() instanceof BlockItem ? 0.75 : 0.5);
                matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);

                var lightAbove = LevelRenderer.getLightColor(world, pos.above());
                itemRenderer.renderStatic(stack, mode, lightAbove, OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, world, 0);

                matrices.popPose();
            }
            case FAST -> {
                matrices.pushPose();
                var lightAbove = LevelRenderer.getLightColor(world, pos.above());
                itemRenderer.renderStatic(stack, mode, lightAbove, OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, world, 0);
                matrices.popPose();
            }
        }
    }

    public static void addParticle(Level world, @Nullable ParticleOptions parameters, Vec3 position, Vec3 velocity) {
        if (parameters != null) {
            world.addParticle(parameters, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        }
    }

    public static void drawFluid(PoseStack matrices, VertexConsumer buffer, BlockAndTintGetter view, BlockPos pos, Fluid fluid, FluidState state, boolean still, int light, int overlay, float x, float width, float y, float height, float depth) {
        var handler = Objects.requireNonNull(FluidRenderHandlerRegistry.INSTANCE.get(fluid));
        var sprites = handler.getFluidSprites(view, pos, state);
        var tint = handler.getFluidColor(view, pos, state);
        drawTexturedSprite(matrices, buffer, still ? sprites[0] : sprites[1], Color.ofRgb(tint).argb(), light, overlay, x, width, y, height, depth);
    }

    /**
     * Use when RenderLayer requires a texture input (e.g. entity_* ones)
     */
    public static void drawSprite(PoseStack matrices, VertexConsumer buffer, TextureAtlasSprite sprite, int tint, int light, int overlay, float x, float width, float y, float height, float depth) {
        drawTexture(matrices, buffer, tint, light, overlay, x, x + width, y, y + height, depth, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

    /**
     * Use when RenderLayer doesn't require a texture input (e.g. cutout or translucent)
     */
    public static void drawTexturedSprite(PoseStack matrices, VertexConsumer buffer, TextureAtlasSprite sprite, int tint, int light, int overlay, float x, float width, float y, float height, float depth) {
        drawTexture(matrices, sprite.wrap(buffer), tint, light, overlay, x, x + width, y, y + height, depth, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

    public static void drawTexture(PoseStack matrices, VertexConsumer buffer, int tint, int light, int overlay, float x, float width, float y, float height, float depth) {
        drawTexture(matrices, buffer, tint, light, overlay, x, x + width, y, y + height, depth);
    }

    public static void drawTexture(PoseStack matrices, VertexConsumer buffer, int tint, int light, int overlay, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        var positionMatrix = matrices.last().pose();
        buffer.addVertex(positionMatrix, x1, y1, z).setUv(u1, v1).setColor(tint).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        buffer.addVertex(positionMatrix, x1, y2, z).setUv(u1, v2).setColor(tint).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        buffer.addVertex(positionMatrix, x2, y2, z).setUv(u2, v2).setColor(tint).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
        buffer.addVertex(positionMatrix, x2, y1, z).setUv(u2, v1).setColor(tint).setOverlay(overlay).setLight(light).setNormal(0, 1, 0);
    }
}
