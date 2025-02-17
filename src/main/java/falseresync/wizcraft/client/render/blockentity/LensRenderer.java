package falseresync.wizcraft.client.render.blockentity;

import falseresync.wizcraft.common.blockentity.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class LensRenderer implements BlockEntityRenderer<LensBlockEntity> {
    public static final SpriteIdentifier ON_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, wid("block/lens_on"));
    public static final SpriteIdentifier OFF_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, wid("block/lens_off"));
    public static final EntityModelLayer LAYER = new EntityModelLayer(wid("lens"), "main");
    private static ModelPart model;

    public LensRenderer(BlockEntityRendererFactory.Context ctx) {
        if (model == null) {
            model = ctx.getLayerModelPart(LAYER);
        }
    }

    public static TexturedModelData getTexturedModelData() {
        var modelData = new ModelData();
        var modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(3, 1, 3, 10, 14, 10), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void render(LensBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.isOn()) {
            matrices.push();
            matrices.translate(0, Math.sin((entity.getWorld().getTime() + tickDelta) / 16) / 16, 0);
            model.render(matrices, ON_TEX.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentEmissive), light, overlay);
            matrices.pop();
        } else {
            model.render(matrices, OFF_TEX.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentEmissive), light, overlay);
        }
    }

    public static class ItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
        @Override
        public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
            model.render(matrices, ON_TEX.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentEmissive), light, overlay);
        }
    }
}
