package falseresync.wizcraft.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import falseresync.wizcraft.common.blockentity.LensBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class LensRenderer implements BlockEntityRenderer<LensBlockEntity> {
    public static final Material ON_TEX = new Material(TextureAtlas.LOCATION_BLOCKS, wid("block/lens_on"));
    public static final Material OFF_TEX = new Material(TextureAtlas.LOCATION_BLOCKS, wid("block/lens_off"));
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(wid("lens"), "main");
    private static ModelPart model;

    public LensRenderer(BlockEntityRendererProvider.Context ctx) {
        if (model == null) {
            model = ctx.bakeLayer(LAYER);
        }
    }

    public static LayerDefinition getTexturedModelData() {
        var modelData = new MeshDefinition();
        var modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(3, 1, 3, 10, 14, 10), PartPose.ZERO);
        return LayerDefinition.create(modelData, 64, 32);
    }

    @Override
    public void render(LensBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (entity.isOn()) {
            poseStack.pushPose();
            poseStack.translate(0, Math.sin((entity.getLevel().getGameTime() + partialTick) / 16) / 16, 0);
            model.render(poseStack, ON_TEX.buffer(bufferSource, RenderType::entityTranslucentEmissive), packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            model.render(poseStack, OFF_TEX.buffer(bufferSource, RenderType::entityTranslucentEmissive), packedLight, packedOverlay);
        }
    }

    public static class ItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
        @Override
        public void render(ItemStack stack, ItemDisplayContext mode, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            model.render(poseStack, ON_TEX.buffer(bufferSource, RenderType::entityTranslucentEmissive), packedLight, packedOverlay);
        }
    }
}
