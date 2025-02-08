package falseresync.wizcraft.client.render.entity;

import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class EnergyVeilRenderer extends EntityRenderer<EnergyVeilEntity> {
    public static final Identifier TEXTURE = wid("textures/entity/energy_veil.png");
    public static final EntityModelLayer LAYER = new EntityModelLayer(wid("energy_veil"), "main");
    private final EnergyVeilModel model;
    private final RenderLayer renderLayer;
    private final MinecraftClient client;

    public EnergyVeilRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new EnergyVeilModel(ctx.getPart(LAYER));
        renderLayer = RenderLayer.getEntityTranslucent(TEXTURE);
        client = MinecraftClient.getInstance();
    }

    // https://stackoverflow.com/a/22401169
    // https://en.wikipedia.org/wiki/Vector_projection
    @Override
    public void render(EnergyVeilEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        var rotation = client.gameRenderer.getCamera().getRotation().rotateY(Math.toRadians(90));
        var rotationAxis = new Vector3f(rotation.x, rotation.y, rotation.z);
        var direction = Direction.UP.getUnitVector();
        var projection = direction.mul(rotationAxis.length() * rotationAxis.angleCos(direction));
        var twist = new Quaternionf(projection.x, projection.y, projection.z, rotation.w).normalize();

        matrices.multiply(twist);

        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.setAngles(entity, 0, 0, tickDelta, 0, 0);
        model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

    @Override
    public Identifier getTexture(EnergyVeilEntity entity) {
        return TEXTURE;
    }
}
