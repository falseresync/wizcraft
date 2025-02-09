package falseresync.wizcraft.client.render.entity;

import com.mojang.blaze3d.platform.GlConst;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;

import java.util.function.BiFunction;

import static falseresync.wizcraft.common.Wizcraft.wid;
import static net.minecraft.client.render.RenderPhase.*;

public class EnergyVeilFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = wid("textures/entity/energy_veil.png");
    public static final EntityModelLayer LAYER = new EntityModelLayer(wid("energy_veil"), "main");
    private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_TRANSLUCENT_NO_DEPTH_TEST = Util.memoize((texture, affectsOutline) -> {
            var parameters = RenderLayer.MultiPhaseParameters.builder()
                    .program(ENTITY_TRANSLUCENT_PROGRAM)
                    .texture(new RenderPhase.Texture(texture, false, false))
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .cull(DISABLE_CULLING)
                    .lightmap(ENABLE_LIGHTMAP)
                    .overlay(ENABLE_OVERLAY_COLOR)
                    .build(affectsOutline);
            return RenderLayer.of(
                    "entity_translucent_no_depth_test",
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    VertexFormat.DrawMode.QUADS,
                    1536, true, true, parameters
            );
    });
    private final EnergyVeilModel model;
    private final RenderLayer renderLayer;

    public EnergyVeilFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        model = new EnergyVeilModel(loader.getModelPart(LAYER));
        renderLayer = ENTITY_TRANSLUCENT_NO_DEPTH_TEST.apply(TEXTURE, true);
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
