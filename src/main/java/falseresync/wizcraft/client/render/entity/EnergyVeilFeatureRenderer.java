package falseresync.wizcraft.client.render.entity;

import falseresync.wizcraft.common.WizcraftConfig;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class EnergyVeilFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = wid("textures/entity/energy_veil.png");
    public static final EntityModelLayer LAYER = new EntityModelLayer(wid("energy_veil"), "main");
    private final EnergyVeilModel model;
    private final RenderLayer renderLayer;

    public EnergyVeilFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        model = new EnergyVeilModel(loader.getModelPart(LAYER));
        renderLayer = RenderLayer.getEntityTranslucentEmissive(TEXTURE);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        var veil = findVeil(entity);
        if (veil == null) return;

        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.animateModel(veil, limbAngle, limbDistance, tickDelta);

        for (int i = 0; i < 4; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 45));
            matrices.translate(-veil.getVeilVisibleRadius(), -1, 0);
            model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);
            matrices.translate(veil.getVeilVisibleRadius() * 2, 0, 0);
            model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }

    public void renderInFirstPerson(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float tickDelta, float animationProgress) {
        var veil = findVeil(entity);
        if (veil == null) return;

        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.animateModel(veil, 0, 0, tickDelta);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        for (int i = 0; i < 3; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45 + i * 45));
            matrices.translate(veil.getVeilVisibleRadius(), -1, 0);
            model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, ((int) (0x44 / WizcraftConfig.fullscreenEffectsTransparency.modifier)) << 24 | 0x00_FF_FF_FF);
            matrices.pop();
        }
    }

    @Nullable
    private EnergyVeilEntity findVeil(T entity) {
        return Optional.ofNullable(entity.getAttached(WizcraftDataAttachments.ENERGY_VEIL_NETWORK_ID))
                .map(id -> entity.getEntityWorld().getEntityById(id))
                .flatMap(foundEntity -> foundEntity instanceof EnergyVeilEntity veil ? Optional.of(veil) : Optional.empty())
                .orElse(null);
    }

    public interface Accessor {
        EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> wizcraft$getEnergyVeilRenderer();
    }
}
