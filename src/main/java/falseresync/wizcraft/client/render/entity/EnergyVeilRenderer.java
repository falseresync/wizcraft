package falseresync.wizcraft.client.render.entity;

import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class EnergyVeilRenderer extends EntityRenderer<EnergyVeilEntity> {
    public static final Identifier TEXTURE = wid("textures/entity/energy_veil.png");
    public static final EntityModelLayer LAYER = new EntityModelLayer(wid("energy_veil"), "main");
    private final EnergyVeilModel model;
    private final RenderLayer renderLayer;

    public EnergyVeilRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new EnergyVeilModel(ctx.getPart(LAYER));
        renderLayer = RenderLayer.getEntityTranslucent(TEXTURE);
    }

    @Override
    public void render(EnergyVeilEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.setAngles(entity, 0, 0, tickDelta, 0, 0);
        model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    public Identifier getTexture(EnergyVeilEntity entity) {
        return TEXTURE;
    }
}
