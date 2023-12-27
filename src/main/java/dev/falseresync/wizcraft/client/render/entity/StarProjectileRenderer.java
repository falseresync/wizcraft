package dev.falseresync.wizcraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.entity.StarProjectileEntity;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class StarProjectileRenderer extends EntityRenderer<StarProjectileEntity> {
    protected static final Identifier TEXTURE = new Identifier(Wizcraft.MODID, "textures/entity/star_projectile.png");
    private final RenderLayer renderLayer;

    public StarProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        renderLayer = RenderLayer.getEntityCutout(TEXTURE);
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
    public Identifier getTexture(StarProjectileEntity entity) {
        return TEXTURE;
    }
}
