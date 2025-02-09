package falseresync.wizcraft.client.render.entity;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class EnergyVeilFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = wid("textures/entity/energy_veil.png");
    public static final EntityModelLayer LAYER = new EntityModelLayer(wid("energy_veil"), "main");
    private final EnergyVeilModel model;
    private final RenderLayer renderLayer;

    public EnergyVeilFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        model = new EnergyVeilModel(loader.getModelPart(LAYER));
        renderLayer = RenderLayer.getEntityTranslucent(TEXTURE);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        matrices.translate(-1.5, -1, 0);

        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.setAngles(entity, 0, 0, tickDelta, 0, 0);
        model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

    public void renderInFirstPerson(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float tickDelta, float animationProgress) {
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        matrices.translate(1.5, -1, 0);

        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.setAngles(entity, 0, 0, tickDelta, 0, 0);
        model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, 0x33_FF_FF_FF);

        matrices.pop();
    }

    public interface Accessor {
        EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> wizcraft$getEnergyVeilRenderer();
    }
}
