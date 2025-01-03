package dev.falseresync.wizcraft.client.render.entity;

import dev.falseresync.wizcraft.common.entity.StarProjectileEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class StarProjectileRenderer extends EntityRenderer<StarProjectileEntity> {
    protected static final Identifier TEXTURE = wid("textures/entity/star_projectile.png");
    private final RenderLayer renderLayer;

    public StarProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        renderLayer = RenderLayer.getEntityCutout(TEXTURE);
    }

    protected static void vertices(VertexConsumer buffer, Matrix4f pm, Matrix3f nm) {
        vertex(buffer, pm, nm, 1, 1, 0, 0, -1);
        vertex(buffer, pm, nm, 1, 0, 0, 1, -1);
        vertex(buffer, pm, nm, 0, 0, 1, 1, -1);
        vertex(buffer, pm, nm, 0, 1, 1, 0, -1);

        vertex(buffer, pm, nm, 0, 1, 0, 0, 1);
        vertex(buffer, pm, nm, 0, 0, 0, 1, 1);
        vertex(buffer, pm, nm, 1, 0, 1, 1, 1);
        vertex(buffer, pm, nm, 1, 1, 1, 0, 1);
    }

    protected static void vertex(VertexConsumer buffer, Matrix4f positionMatrix, Matrix3f normalMatrix, int x, int y, float u, float v, int normal) {
        buffer.vertex(positionMatrix, x, y, 0)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0, 0, normal)
                .next();
    }

    @Override
    public void render(StarProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        var entry = matrices.peek();
        var pm = entry.getPositionMatrix();
        var nm = entry.getNormalMatrix();
        var buffer = vertexConsumers.getBuffer(renderLayer);

        matrices.multiply(dispatcher.getRotation());
        matrices.translate(-0.5f, 0, 0);
        matrices.scale(0.75f, 0.75f, 0.75f);
        vertices(buffer, pm, nm);

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(StarProjectileEntity entity) {
        return TEXTURE;
    }
}
