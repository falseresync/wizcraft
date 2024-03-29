package dev.falseresync.wizcraft.client.render.blockentity;

import dev.falseresync.wizcraft.client.render.CommonRenders;
import dev.falseresync.wizcraft.client.render.RenderingUtil;
import dev.falseresync.wizcraft.common.block.entity.worktable.ChargingWorktableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

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

        CommonRenders.levitateItemAboveBlock(world, entity.getPos(), tickDelta, stack, this.itemRenderer, matrices, vertexConsumers);

        if (entity.isCharging() && world.random.nextFloat() < 0.25) {
            var itemPos = entity.getPos().toCenterPos().add(0, 0.5, 0);
            var particlePos = itemPos.add(
                    world.random.nextFloat() - 0.5,
                    1,
                    world.random.nextFloat() - 0.5);
            var particleVelocity = itemPos.subtract(particlePos).normalize().multiply(100000);
            CommonRenders.addParticle(world, ParticleTypes.GLOW, particlePos, particleVelocity);
        }

        matrices.pop();
    }
}
