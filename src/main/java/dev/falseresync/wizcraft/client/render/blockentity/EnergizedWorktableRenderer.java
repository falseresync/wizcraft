package dev.falseresync.wizcraft.client.render.blockentity;

import dev.falseresync.wizcraft.client.render.CommonRenders;
import dev.falseresync.wizcraft.common.block.entity.EnergizedWorktableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class EnergizedWorktableRenderer implements BlockEntityRenderer<EnergizedWorktableBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public EnergizedWorktableRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(EnergizedWorktableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var slot = entity.getStorage().getSlot(0);
        var world = entity.getWorld();
        if (slot.isResourceBlank() || world == null) {
            return;
        }
        var stack = slot.getResource().toStack();

        matrices.push();

        CommonRenders.levitateItemAboveBlock(world, entity.getPos(), tickDelta, stack, this.itemRenderer, matrices, vertexConsumers);

        matrices.pop();
    }
}
