package falseresync.wizcraft.client.render.blockentity;

import falseresync.wizcraft.client.render.*;
import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.blockentity.*;
import net.fabricmc.api.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;

@Environment(EnvType.CLIENT)
public class ChargingWorktableRenderer implements BlockEntityRenderer<ChargingWorktableBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public ChargingWorktableRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ChargingWorktableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var world = entity.getWorld();
        var stack = entity.getHeldStackCopy();
        if (stack.isEmpty() || world == null) return;

        matrices.push();

        RenderingUtil.levitateItemAboveBlock(
                world, entity.getPos(), tickDelta, stack,
                entity.isCharging() ? ModelTransformationMode.THIRD_PERSON_RIGHT_HAND : ModelTransformationMode.FIXED,
                this.itemRenderer, matrices, vertexConsumers);

        if (entity.isCharging() && world.random.nextFloat() < WizcraftConfig.animationParticlesAmount.modifier) {
            var itemPos = entity.getPos().toCenterPos().add(0, -0.5, 0);
            var particlePos = itemPos.add(world.random.nextFloat() - 0.5, 2, world.random.nextFloat() - 0.5);
            var particleVelocity = particlePos.relativize(itemPos).multiply(5);
            RenderingUtil.addParticle(world, WizcraftParticleTypes.CHARGING, particlePos, particleVelocity);
        }

        matrices.pop();
    }
}
