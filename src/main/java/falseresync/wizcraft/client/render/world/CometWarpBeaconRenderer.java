package falseresync.wizcraft.client.render.world;

import falseresync.lib.math.*;
import falseresync.wizcraft.client.render.*;
import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.math.*;
import org.joml.*;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class CometWarpBeaconRenderer implements WorldRenderEvents.AfterEntities {
    private static final RenderLayer BASE_LAYER = RenderLayer.getEntityTranslucentEmissive(wid("textures/world/comet_warp_beacon.png"));
    private static final SpriteIdentifier CROWN_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, wid("world/comet_warp_beacon_crown"));
    private static final int TINT_BASE = Color.ofHsv(0f, 0f, 1, 0.5f).argb();
    private static final int TINT_CROWN = Color.WHITE.argb();

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void afterEntities(WorldRenderContext context) {
        var player = MinecraftClient.getInstance().player;
        if (!player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)) {
            return;
        }

        var wandStack = player.getMainHandStack();
        if (!wandStack.isIn(WizcraftItemTags.WANDS)) {
            return;
        }

        var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
        if (anchor == null
                || anchor.dimension() != context.world().getRegistryKey()
                || !anchor.pos().isWithinDistance(player.getPos(), Wizcraft.getConfig().trueseerGogglesDisplayRange)
                || !context.frustum().isVisible(Box.from(anchor.pos().toCenterPos()))) {
            return;
        }

        var matrices = context.matrixStack();
        var light = WorldRenderer.getLightmapCoordinates(context.world(), anchor.pos().up());
        var overlay = OverlayTexture.DEFAULT_UV;

        matrices.push();

        // Adjust location
        var translation = context.camera().getPos().relativize(Vec3d.of(anchor.pos()));
        matrices.translate(translation.x, translation.y, translation.z);

        var buffer = context.consumers().getBuffer(BASE_LAYER);
        drawBase(context, matrices, buffer, light, overlay);

        matrices.push();
        var adjustment = new Matrix4f()
                .rotateAround(RotationAxis.POSITIVE_X.rotationDegrees(180), 0, 0.5f, 0)
                .translate(0, 0.25f, -1)
                .scaleAround(0.5f, 0.5f, 0.5f, 0.5f);
        matrices.multiplyPositionMatrix(adjustment);

        var perPanelAdjustment = new Matrix4f()
                .rotateAround(RotationAxis.POSITIVE_Y.rotationDegrees(60), 0.5f, 0, 0.5f);
        var sprite = CROWN_TEX.getSprite();
        var crownBuffer = CROWN_TEX.getVertexConsumer(context.consumers(), RenderLayer::getEntityTranslucentEmissive);
        for (int i = 0; i < 6; i++) {
            matrices.multiplyPositionMatrix(perPanelAdjustment);
            RenderingUtil.drawSprite(matrices, crownBuffer, sprite, TINT_BASE, light, overlay, 0, 1, 0, 1, -0.365f);
        }

        matrices.pop();

        matrices.pop();
    }

    private static void drawBase(WorldRenderContext context, MatrixStack matrices, VertexConsumer buffer, int light, int overlay) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        var rotation = (context.world().getTime() + context.tickCounter().getTickDelta(true)) / 20;
        var perPanelAdjustment = new Matrix4f()
                .rotateAround(RotationAxis.POSITIVE_Z.rotationDegrees(30), 0.5f, 0.5f, 0)
                .translate(0, 0, -0.02f);

        drawBasePart(matrices, buffer, light, overlay, rotation, RotationAxis.POSITIVE_Z, -0.01f, perPanelAdjustment);
        drawBasePart(matrices, buffer, light, overlay, rotation, RotationAxis.NEGATIVE_Z, -0.02f, perPanelAdjustment);

        matrices.pop();
    }

    private static void drawBasePart(MatrixStack matrices, VertexConsumer buffer, int light, int overlay, float rotation, RotationAxis rotationAxis, float initialOffset, Matrix4f perPanelAdjustment) {
        matrices.push();

        matrices.multiplyPositionMatrix(new Matrix4f().rotateAround(rotationAxis.rotation(rotation), 0.5f, 0.5f, 0));
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.multiplyPositionMatrix(perPanelAdjustment);
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.multiplyPositionMatrix(perPanelAdjustment);
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.pop();
    }
}
