package falseresync.wizcraft.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import falseresync.wizcraft.client.render.RenderingUtil;
import falseresync.wizcraft.common.blockentity.LensingPedestalBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;

@Environment(EnvType.CLIENT)
public class LensingPedestalRenderer implements BlockEntityRenderer<LensingPedestalBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public LensingPedestalRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(LensingPedestalBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        var world = entity.getLevel();
        var stack = entity.getHeldStackCopy();
        if (entity.isLinked() || stack.isEmpty() || world == null) return;

        matrices.pushPose();

        RenderingUtil.levitateItemAboveBlock(world, entity.getBlockPos(), tickDelta, stack, this.itemRenderer, matrices, vertexConsumers);

        matrices.popPose();
    }
}
