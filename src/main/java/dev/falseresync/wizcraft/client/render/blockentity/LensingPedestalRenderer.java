package dev.falseresync.wizcraft.client.render.blockentity;

import dev.falseresync.wizcraft.client.render.CommonRenders;
import dev.falseresync.wizcraft.common.block.entity.LensingPedestalBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

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

        CommonRenders.levitateItemAboveBlock(world, entity.getPos(), tickDelta, stack, this.itemRenderer, matrices, vertexConsumers);

        matrices.pop();
    }
}
