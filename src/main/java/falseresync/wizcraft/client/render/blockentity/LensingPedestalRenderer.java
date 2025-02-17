package falseresync.wizcraft.client.render.blockentity;

import falseresync.wizcraft.client.render.*;
import falseresync.wizcraft.common.blockentity.*;
import net.fabricmc.api.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.util.math.*;

@Environment(EnvType.CLIENT)
public class LensingPedestalRenderer implements BlockEntityRenderer<LensingPedestalBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public LensingPedestalRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(LensingPedestalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var world = entity.getWorld();
        var stack = entity.getHeldStackCopy();
        if (entity.isLinked() || stack.isEmpty() || world == null) return;

        matrices.push();

        RenderingUtil.levitateItemAboveBlock(world, entity.getPos(), tickDelta, stack, this.itemRenderer, matrices, vertexConsumers);

        matrices.pop();
    }
}
