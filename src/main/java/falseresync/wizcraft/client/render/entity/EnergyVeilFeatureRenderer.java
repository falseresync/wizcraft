package falseresync.wizcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import falseresync.lib.math.VectorMath;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;

import java.util.Optional;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class EnergyVeilFeatureRenderer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    public static final ResourceLocation TEXTURE = wid("textures/entity/energy_veil.png");
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(wid("energy_veil"), "main");
    private final EnergyVeilModel model;
    private final RenderType renderLayer;

    public EnergyVeilFeatureRenderer(RenderLayerParent<T, PlayerModel<T>> context, EntityModelSet loader) {
        super(context);
        model = new EnergyVeilModel(loader.bakeLayer(LAYER));
        renderLayer = RenderType.entityTranslucentEmissive(TEXTURE);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbAngle, float limbDistance, float partialTick, float animationProgress, float headYaw, float headPitch) {
        var veil = findVeil(entity);
        if (veil == null) return;

        poseStack.pushPose();
        var buffer = bufferSource.getBuffer(renderLayer);
        model.animateModel(veil, limbAngle, limbDistance, partialTick);

        for (int i = 0; i < 4; i++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(i * 45));
            poseStack.translate(-veil.getVeilVisibleRadius(), -1, 0);
            model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.translate(veil.getVeilVisibleRadius() * 2, 0, 0);
            model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public void renderInFirstPerson(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float partialTick, float animationProgress) {
        var veil = findVeil(entity);
        if (veil == null) return;

        poseStack.pushPose();
        poseStack.setIdentity();

        var buffer = bufferSource.getBuffer(renderLayer);
        model.animateModel(veil, 0, 0, partialTick);

        var rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        var direction = Direction.UP.step();
        var swingAndTwist = VectorMath.swingTwistDecomposition(rotation, direction);
        poseStack.mulPose(swingAndTwist.getRight());

        for (int i = 0; i < 3; i++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(45 + i * 45));
            poseStack.translate(veil.getVeilVisibleRadius(), -1, 0);
            model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, ((int) (0x44 / Wizcraft.getConfig().fullscreenEffectsTransparency.modifier)) << 24 | 0x00_FF_FF_FF);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Nullable
    private EnergyVeilEntity findVeil(T entity) {
        return Optional.ofNullable(entity.getAttached(WizcraftAttachments.ENERGY_VEIL_NETWORK_ID))
                .map(id -> entity.getCommandSenderWorld().getEntity(id))
                .flatMap(foundEntity -> foundEntity instanceof EnergyVeilEntity veil ? Optional.of(veil) : Optional.empty())
                .orElse(null);
    }

    public interface Accessor {
        EnergyVeilFeatureRenderer<AbstractClientPlayer> wizcraft$getEnergyVeilRenderer();
    }
}
