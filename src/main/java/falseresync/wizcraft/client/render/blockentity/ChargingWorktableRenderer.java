package falseresync.wizcraft.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import falseresync.wizcraft.client.render.RenderingUtil;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftParticleTypes;
import falseresync.wizcraft.common.blockentity.ChargingWorktableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class ChargingWorktableRenderer implements BlockEntityRenderer<ChargingWorktableBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public ChargingWorktableRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ChargingWorktableBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        var world = entity.getLevel();
        var stack = entity.getHeldStackCopy();
        if (stack.isEmpty() || world == null) return;

        matrices.pushPose();

        RenderingUtil.levitateItemAboveBlock(
                world, entity.getBlockPos(), tickDelta, stack,
                entity.isCharging() ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.FIXED,
                this.itemRenderer, matrices, vertexConsumers);

        if (entity.isCharging() && world.random.nextFloat() < Wizcraft.getConfig().animationParticlesAmount.modifier) {
            var itemPos = entity.getBlockPos().getCenter().add(0, -0.5, 0);
            var particlePos = itemPos.add(world.random.nextFloat() - 0.5, 2, world.random.nextFloat() - 0.5);
            var particleVelocity = particlePos.vectorTo(itemPos).scale(5);
            RenderingUtil.addParticle(world, WizcraftParticleTypes.CHARGING, particlePos, particleVelocity);
        }

        matrices.popPose();
    }
}
