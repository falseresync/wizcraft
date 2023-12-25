package dev.falseresync.wizcraft.client.render.entity;

import dev.falseresync.wizcraft.common.entity.StarProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class StarProjectileRenderer extends EntityRenderer<StarProjectileEntity> {
    private final ItemRenderer itemRenderer;

    public StarProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(StarProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
//        if (entity.getRotat) {
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
//        }

        itemRenderer
                .renderItem(
                        new ItemStack(Items.SNOWBALL),
                        ModelTransformationMode.GROUND,
                        light,
                        OverlayTexture.DEFAULT_UV,
                        matrices,
                        vertexConsumers,
                        entity.getWorld(),
                        entity.getId());
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(StarProjectileEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
