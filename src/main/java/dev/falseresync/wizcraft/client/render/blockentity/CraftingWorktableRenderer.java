package dev.falseresync.wizcraft.client.render.blockentity;

import dev.falseresync.wizcraft.client.render.CommonRenders;
import dev.falseresync.wizcraft.common.block.entity.LensingPedestalBlockEntity;
import dev.falseresync.wizcraft.common.block.entity.worktable.CraftingWorktableBlockEntity;
import dev.falseresync.wizcraft.common.particle.WizParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Stream;

import static dev.falseresync.wizcraft.client.render.RenderingUtil.getSymmetricVec3d;

@Environment(EnvType.CLIENT)
public class CraftingWorktableRenderer implements BlockEntityRenderer<CraftingWorktableBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public CraftingWorktableRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    protected static void animateCraftingProgress(RenderingData worktable, CraftingWorktableBlockEntity.Progress progress, List<RenderingData> pedestals, World world, ItemRenderer itemRenderer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var random = world.getRandom();

        // Stop early because particles live some time
        if (progress.remainingCraftingTime() > 10) {
            for (var pedestal : pedestals) {
                addParticleBeam(worktable, progress, pedestal, world, tickDelta);
            }
        }

        // Stop early and start a bit later to create an effect that particles start to swirl
        if (progress.remainingCraftingTime() > 10 && progress.passedCraftingTime() > 5) {
            for (var pedestal : pedestals) {
                addParticleHurricane(worktable, progress, pedestal, world, tickDelta);
            }
        }

        // Disintegrate the pedestal items
        for (var pedestal : pedestals) {
            addDisintegrationParticles(world, random, pedestal, progress);
        }

        // Disintegrate the worktable item near the end as well
        if (progress.remainingCraftingTime() < 40) {
            addDisintegrationParticles(world, random, worktable, progress);

            // Increase amount of worktable item disintegration particles
            if (progress.remainingCraftingTime() < 10) {
                addDisintegrationParticles(world, random, worktable, progress);
            }
        }

        // Render items until the very end (they'll be too small then anyway
        if (progress.remainingCraftingTime() > 5) {
            levitateItems(worktable, progress.value(), pedestals, world, itemRenderer, tickDelta, matrices, vertexConsumers);
        }

        // Start to render the resulting item
        if (progress.remainingCraftingTime() < 30 && !progress.currentlyCrafted().isEmpty()) {
            var scale = getSymmetricVec3d(getCraftingResultScaleForProgress(progress.value()));
            CommonRenders.levitateItemAboveBlock(world, worktable.pos, Vec3d.ZERO, scale, tickDelta, progress.currentlyCrafted(), itemRenderer, matrices, vertexConsumers);
        }
    }
    protected static double getCraftingResultScaleForProgress(double p) {
        return -1 / (19 * p - 20);
    }

    protected static void levitateItems(RenderingData worktable, float craftingProgress, List<RenderingData> pedestals, World world, ItemRenderer itemRenderer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var scale = getSymmetricVec3d(1 - craftingProgress);
        CommonRenders.levitateItemAboveBlock(world, worktable.pos, Vec3d.ZERO, scale, tickDelta, worktable.stack, itemRenderer, matrices, vertexConsumers);

        for (var pedestal : pedestals) {
            var translation = worktable.above.relativize(pedestal.above);
            CommonRenders.levitateItemAboveBlock(world, pedestal.pos, translation, scale, tickDelta, pedestal.stack, itemRenderer, matrices, vertexConsumers);
        }
    }

    // ALL, and I mean - ALL parenthesis matter here
    protected static double getVelocityForProgress(double p) {
        return 0.2 * (-0.4 - 1.05 / (1 * (1.1 * Math.log10(-2 * p + 2.0) + 1 * (2 * p - 2))));
    }

    protected static void addParticleBeam(RenderingData worktable, CraftingWorktableBlockEntity.Progress progress, RenderingData pedestal, World world, float tickDelta) {
        if (pedestal.stack.isEmpty()) return;

        var temporalOffset = Math.abs(MathHelper.sin((progress.remainingCraftingTime() + tickDelta)));
        var path = pedestal.above.relativize(worktable.above);
        var pos = pedestal.above.add(path.multiply(Math.min(0.6, temporalOffset * progress.value())));
        var velocity = path.normalize().multiply(getVelocityForProgress(progress.value()));
        CommonRenders.addParticle(world, pedestal.particle, pos, velocity);
    }

    // ALL, and I mean - ALL parenthesis matter here
    protected static double getRadiusForProgress(double p) {
        return 1.75 * ( -4.3 / ( 3.6 * p - 6.35 ) - 1.85 * p + 0.3 );
    }

    protected static void addParticleHurricane(RenderingData worktable, CraftingWorktableBlockEntity.Progress progress, RenderingData pedestal, World world, float tickDelta) {
        if (pedestal.stack.isEmpty()) return;

        var temporalOffset = Math.abs(MathHelper.sin((progress.remainingCraftingTime() + tickDelta)));
        for (int i = 0; i < (1 - progress.value()) * 3; i++) {
            var theta = 2f * MathHelper.PI * temporalOffset + i * temporalOffset;
            var r = getRadiusForProgress(progress.value());
            var hx = r * MathHelper.cos(theta);
            var hz = r * MathHelper.sin(theta);
            var pos = worktable.above.add(hx, 0, hz);
            // Vector tangent to a circle https://stackoverflow.com/q/40710168
            var velocity = new Vec3d(hz, 0, -hx).normalize().multiply(getVelocityForProgress(progress.value()));
            var randomizedPos = pos.add(
                    (hx / 16) * world.random.nextFloat(),
                    (world.random.nextGaussian() - 0.5) / 16,
                    (hz / 16) * world.random.nextFloat());
            CommonRenders.addParticle(world, pedestal.particle, randomizedPos, velocity);
        }
    }

    protected static void addDisintegrationParticles(World world, Random random, RenderingData renderingData, CraftingWorktableBlockEntity.Progress progress) {
        if (renderingData.stack.isEmpty()) return;

        var velocity = new Vec3d((random.nextFloat() - 0.5) / 8, random.nextGaussian() / 16, (random.nextFloat() - 0.5) / 8);
        var amount = progress.value() * 10 * (renderingData.stack.getItem() instanceof BlockItem ? 3 : 1);
        for (int i = 0; i < amount; i++) {
            CommonRenders.addParticle(world, renderingData.particle, renderingData.above, velocity);
        }
    }

    @Override
    public void render(CraftingWorktableBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var world = blockEntity.getWorld();
        if (world == null) return;

        var worktable = new RenderingData(blockEntity.getPos(), blockEntity.getHeldStackCopy());
        var progress = blockEntity.getProgress();
        var pedestals = blockEntity.getNonEmptyPedestalPositions().stream()
                .map(world::getBlockEntity)
                .flatMap(it -> it instanceof LensingPedestalBlockEntity pedestal ? Stream.of(pedestal) : Stream.empty())
                .map(pedestal -> new RenderingData(pedestal.getPos(), pedestal.getHeldStackCopy()))
                .toList();

        if (pedestals.isEmpty() && worktable.stack.isEmpty()) return;

        matrices.push();

        if (progress.remainingCraftingTime() > 0) {
            animateCraftingProgress(worktable, progress, pedestals, world, itemRenderer, tickDelta, matrices, vertexConsumers);
        } else {
            levitateItems(worktable, 0, pedestals, world, itemRenderer, tickDelta, matrices, vertexConsumers);
        }

        matrices.pop();
    }

    public record RenderingData(BlockPos pos, Vec3d center, Vec3d above, ItemStack stack, ParticleEffect particle) {
        private RenderingData(BlockPos pos, ItemStack stack) {
            this(pos, pos.toCenterPos(), pos.toCenterPos().add(0, 0.75, 0), stack, new ItemStackParticleEffect(WizParticleTypes.SPAGHETTIFICATION, stack));
        }
    }
}
