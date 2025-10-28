package falseresync.wizcraft.client.render.entity;

import falseresync.lib.math.*;
import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.entity.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static falseresync.wizcraft.common.Wizcraft.*;

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

        matrices.push();
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
        matrices.pop();
    }

    public void renderInFirstPerson(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float tickDelta, float animationProgress) {
        var veil = findVeil(entity);
        if (veil == null) return;

        matrices.push();
        matrices.loadIdentity();

        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.animateModel(veil, 0, 0, tickDelta);

        var rotation = MinecraftClient.getInstance().gameRenderer.getCamera().getRotation();
        var direction = Direction.UP.getUnitVector();
        var swingAndTwist = VectorMath.swingTwistDecomposition(rotation, direction);
        matrices.multiply(swingAndTwist.getRight());

        for (int i = 0; i < 3; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45 + i * 45));
            matrices.translate(veil.getVeilVisibleRadius(), -1, 0);
            model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, ((int) (0x44 / Wizcraft.getConfig().fullscreenEffectsTransparency.modifier)) << 24 | 0x00_FF_FF_FF);
            matrices.pop();
        }

        matrices.pop();
    }

    @Nullable
    private EnergyVeilEntity findVeil(T entity) {
        return Optional.ofNullable(entity.getAttached(WizcraftAttachments.ENERGY_VEIL_NETWORK_ID))
                .map(id -> entity.getEntityWorld().getEntityById(id))
                .flatMap(foundEntity -> foundEntity instanceof EnergyVeilEntity veil ? Optional.of(veil) : Optional.empty())
                .orElse(null);
    }

    public interface Accessor {
        EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> wizcraft$getEnergyVeilRenderer();
    }
}
