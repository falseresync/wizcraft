package dev.falseresync.wizcraft.client.render.blockentity;

import dev.falseresync.wizcraft.client.render.CommonRenders;
import dev.falseresync.wizcraft.common.block.entity.LensingPedestalBlockEntity;
import dev.falseresync.wizcraft.common.block.entity.WorktableBlockEntity;
import dev.falseresync.wizcraft.common.particle.WizParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class WorktableRenderer implements BlockEntityRenderer<WorktableBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public WorktableRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(WorktableBlockEntity worktable, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var world = worktable.getWorld();
        var worktableStack = worktable.getHeldStackCopy();
        if (worktableStack.isEmpty() || world == null) return;

        var pedestals = worktable.getNonEmptyPedestalPositions().stream()
                .map(world::getBlockEntity)
                .flatMap(blockEntity -> blockEntity instanceof LensingPedestalBlockEntity pedestal ? Stream.of(pedestal) : Stream.empty())
                .toList();

        matrices.push();

        if (!pedestals.isEmpty() && worktable.getRemainingCraftingTime() > 0) {
            animateCraftingProgress(world, worktable, worktableStack, pedestals, tickDelta, matrices, vertexConsumers);
        } else {
            CommonRenders.levitateItemAboveBlock(world, worktable.getPos(), tickDelta, worktableStack, this.itemRenderer, matrices, vertexConsumers);
        }

        matrices.pop();
    }

    protected void animateCraftingProgress(World world, WorktableBlockEntity worktable, ItemStack worktableStack, List<LensingPedestalBlockEntity> pedestals, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var aboveWorktablePos = worktable.getPos().toCenterPos().add(0, 0.75, 0);
        var random = world.getRandom();
        var passedCraftingTime = worktable.getCraftingTime() - worktable.getRemainingCraftingTime();
        var craftingProgress = (double) passedCraftingTime / worktable.getCraftingTime();

        // Stop early because particles live some time
        if (worktable.getRemainingCraftingTime() > 10) {
            for (var pedestal : pedestals) {
                // Skip some ticks to reduce the number of particles
                if (random.nextFloat() < 0.75) {
                    addParticleBeam(world, pedestal, aboveWorktablePos, tickDelta, craftingProgress);
                }
            }
        }

        // Stop a bit later and start a bit later to create an effect that particles start to swirl
        if (worktable.getRemainingCraftingTime() > 5 && passedCraftingTime > 5) {
            for (var pedestal : pedestals) {
                addParticleHurricane(world, pedestal, aboveWorktablePos, tickDelta, craftingProgress);
            }
        }

        if (worktable.getRemainingCraftingTime() > 5) {
            CommonRenders.levitateItemAboveBlock(world, worktable.getPos(), tickDelta, worktableStack, this.itemRenderer, matrices, vertexConsumers);
        } else {
            explodeItem(world, random, worktableStack, aboveWorktablePos);

            for (var pedestal : pedestals) {
                var abovePedestalPos = pedestal.getPos().toCenterPos().add(0, 0.75, 0);
                explodeItem(world, random, pedestal.getHeldStackCopy(), abovePedestalPos);
            }
        }
    }

    protected void addParticleBeam(World world, LensingPedestalBlockEntity pedestal, Vec3d aboveWorktablePos, float tickDelta, double craftingProgress) {
        var parameters = new ItemStackParticleEffect(WizParticleTypes.SPAGHETTIFICATION, pedestal.getHeldStackCopy());
        var abovePedestalPos = pedestal.getPos().toCenterPos().add(0, 0.75, 0);
        var temporalOffset = Math.abs(MathHelper.sin((world.getTime() + tickDelta)));
        var path = abovePedestalPos.subtract(aboveWorktablePos).negate().multiply(0.4);
        var pos = abovePedestalPos.add(path.multiply(temporalOffset));
        var velocity = path.normalize().multiply(0.2 * craftingProgress);
        CommonRenders.addParticle(world, parameters, pos, velocity);
    }

    protected void addParticleHurricane(World world,LensingPedestalBlockEntity pedestal, Vec3d aboveWorktablePos, float tickDelta, double craftingProgress) {
        var parameters = new ItemStackParticleEffect(WizParticleTypes.SPAGHETTIFICATION, pedestal.getHeldStackCopy());
        var temporalOffset = Math.abs(MathHelper.sin((world.getTime() + tickDelta)));
        var theta = 2f * MathHelper.PI * temporalOffset;
        var hx = MathHelper.cos(theta);
        var hz = MathHelper.sin(theta);
        var pos = aboveWorktablePos.add(hx / (4 * craftingProgress), 0, hz / (4 * craftingProgress));
        // Vector tangent to a circle https://stackoverflow.com/q/40710168
        var velocity = new Vec3d(hz / 4, 0, -hx / 4).normalize().multiply(0.075);
        CommonRenders.addParticle(world, parameters, pos, velocity);
    }

    protected void explodeItem(World world, Random random, ItemStack stack, Vec3d pos) {
        var parameters = new ItemStackParticleEffect(WizParticleTypes.SPAGHETTIFICATION, stack);
        for (int i = 0; i < random.nextBetween(2, 4); i++) {
            var velocity = new Vec3d((random.nextFloat() - 0.5) / 16, random.nextGaussian() / 16, (random.nextFloat() - 0.5) / 16);
            CommonRenders.addParticle(world, parameters, pos, velocity);
        }
    }
}
