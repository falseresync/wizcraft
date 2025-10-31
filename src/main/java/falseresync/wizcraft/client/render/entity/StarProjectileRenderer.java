package falseresync.wizcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import falseresync.wizcraft.common.entity.StarProjectileEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class StarProjectileRenderer extends EntityRenderer<StarProjectileEntity> {
    protected static final ResourceLocation TEXTURE = wid("textures/entity/star_projectile.png");
    private final RenderType renderLayer;

    public StarProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        renderLayer = RenderType.entityCutout(TEXTURE);
    }

    protected static void vertices(VertexConsumer buffer, Matrix4f pm, PoseStack.Pose entry) {
        vertex(buffer, pm, entry, 1, 1, 0, 0, -1);
        vertex(buffer, pm, entry, 1, 0, 0, 1, -1);
        vertex(buffer, pm, entry, 0, 0, 1, 1, -1);
        vertex(buffer, pm, entry, 0, 1, 1, 0, -1);

        vertex(buffer, pm, entry, 0, 1, 0, 0, 1);
        vertex(buffer, pm, entry, 0, 0, 0, 1, 1);
        vertex(buffer, pm, entry, 1, 0, 1, 1, 1);
        vertex(buffer, pm, entry, 1, 1, 1, 0, 1);
    }

    protected static void vertex(VertexConsumer buffer, Matrix4f positionMatrix, PoseStack.Pose entry, int x, int y, float u, float v, int normal) {
        buffer.addVertex(positionMatrix, x, y, 0)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(entry, 0, 0, normal);
    }

    @Override
    public void render(StarProjectileEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        var entry = poseStack.last();
        var pm = entry.pose();
        var buffer = bufferSource.getBuffer(renderLayer);

        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.translate(-0.5f, 0, 0);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        vertices(buffer, pm, entry);

        poseStack.popPose();

        super.render(entity, yaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(StarProjectileEntity entity) {
        return TEXTURE;
    }
}