package falseresync.wizcraft.client.render.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import falseresync.lib.math.Color;
import falseresync.wizcraft.client.render.RenderingUtil;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class CometWarpBeaconRenderer implements WorldRenderEvents.AfterEntities {
    private static final RenderType BASE_LAYER = RenderType.entityTranslucentEmissive(wid("textures/world/comet_warp_beacon.png"));
    private static final Material CROWN_TEX = new Material(TextureAtlas.LOCATION_BLOCKS, wid("world/comet_warp_beacon_crown"));
    private static final int TINT_BASE = Color.ofHsv(0f, 0f, 1, 0.5f).argb();
    private static final int TINT_CROWN = Color.WHITE.argb();

    private static void drawBase(WorldRenderContext context, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        var rotation = (context.world().getGameTime() + context.tickCounter().getGameTimeDeltaPartialTick(true)) / 20;
        var perPanelAdjustment = new Matrix4f()
                .rotateAround(Axis.ZP.rotationDegrees(30), 0.5f, 0.5f, 0)
                .translate(0, 0, -0.02f);

        drawBasePart(poseStack, buffer, packedLight, packedOverlay, rotation, Axis.ZP, -0.01f, perPanelAdjustment);
        drawBasePart(poseStack, buffer, packedLight, packedOverlay, rotation, Axis.ZN, -0.02f, perPanelAdjustment);

        poseStack.popPose();
    }

    private static void drawBasePart(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float rotation, Axis rotationAxis, float initialOffset, Matrix4f perPanelAdjustment) {
        poseStack.pushPose();

        poseStack.mulPose(new Matrix4f().rotateAround(rotationAxis.rotation(rotation), 0.5f, 0.5f, 0));
        RenderingUtil.drawTexture(poseStack, buffer, TINT_BASE, packedLight, packedOverlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        poseStack.mulPose(perPanelAdjustment);
        RenderingUtil.drawTexture(poseStack, buffer, TINT_BASE, packedLight, packedOverlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        poseStack.mulPose(perPanelAdjustment);
        RenderingUtil.drawTexture(poseStack, buffer, TINT_BASE, packedLight, packedOverlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        poseStack.popPose();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void afterEntities(WorldRenderContext context) {
        var player = Minecraft.getInstance().player;
        if (!player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)) {
            return;
        }

        var wandStack = player.getMainHandItem();
        if (!wandStack.is(WizcraftItemTags.WANDS)) {
            return;
        }

        var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
        if (anchor == null
                || anchor.dimension() != context.world().dimension()
                || !anchor.pos().closerToCenterThan(player.position(), Wizcraft.getConfig().trueseerGogglesDisplayRange)
                || !context.frustum().isVisible(AABB.unitCubeFromLowerCorner(anchor.pos().getCenter()))) {
            return;
        }

        var poseStack = context.matrixStack();
        var light = LevelRenderer.getLightColor(context.world(), anchor.pos().above());
        var overlay = OverlayTexture.NO_OVERLAY;

        poseStack.pushPose();

        // Adjust location
        var translation = context.camera().getPosition().vectorTo(Vec3.atLowerCornerOf(anchor.pos()));
        poseStack.translate(translation.x, translation.y, translation.z);

        var buffer = context.consumers().getBuffer(BASE_LAYER);
        drawBase(context, poseStack, buffer, light, overlay);

        poseStack.pushPose();
        var adjustment = new Matrix4f()
                .rotateAround(Axis.XP.rotationDegrees(180), 0, 0.5f, 0)
                .translate(0, 0.25f, -1)
                .scaleAround(0.5f, 0.5f, 0.5f, 0.5f);
        poseStack.mulPose(adjustment);

        var perPanelAdjustment = new Matrix4f()
                .rotateAround(Axis.YP.rotationDegrees(60), 0.5f, 0, 0.5f);
        var sprite = CROWN_TEX.sprite();
        var crownBuffer = CROWN_TEX.buffer(context.consumers(), RenderType::entityTranslucentEmissive);
        for (int i = 0; i < 6; i++) {
            poseStack.mulPose(perPanelAdjustment);
            RenderingUtil.drawSprite(poseStack, crownBuffer, sprite, TINT_BASE, light, overlay, 0, 1, 0, 1, -0.365f);
        }

        poseStack.popPose();

        poseStack.popPose();
    }
}
