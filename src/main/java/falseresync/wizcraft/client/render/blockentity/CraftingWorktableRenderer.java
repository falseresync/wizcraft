package falseresync.wizcraft.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import falseresync.wizcraft.client.render.RenderingUtil;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftParticleTypes;
import falseresync.wizcraft.common.blockentity.CraftingWorktableBlockEntity;
import falseresync.wizcraft.common.blockentity.LensingPedestalBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

import java.util.List;
import java.util.stream.Stream;

import static falseresync.wizcraft.client.render.RenderingUtil.getSymmetricVec3d;

@Environment(EnvType.CLIENT)
public class CraftingWorktableRenderer implements BlockEntityRenderer<CraftingWorktableBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public CraftingWorktableRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    protected static void animateCraftingProgress(RenderingData worktable, CraftingWorktableBlockEntity.Progress progress, List<RenderingData> pedestals, Level level, ItemRenderer itemRenderer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        var random = level.getRandom();

        // Stop early because particles live some time
        if (progress.remainingCraftingTime() > 10) {
            for (var pedestal : pedestals) {
                addParticleBeam(worktable, progress, pedestal, level, partialTick);
            }
        }

        // Stop early and start a bit later to create an effect that particles start to swirl
        if (progress.remainingCraftingTime() > 10 && progress.passedCraftingTime() > 5) {
            for (var pedestal : pedestals) {
                addParticleHurricane(worktable, progress, pedestal, level, partialTick);
            }
        }

        // Disintegrate the pedestal items
        for (var pedestal : pedestals) {
            addDisintegrationParticles(level, random, pedestal, progress);
        }

        // Disintegrate the worktable item near the end as well
        if (progress.remainingCraftingTime() < 40) {
            addDisintegrationParticles(level, random, worktable, progress);

            // Increase amount of worktable item disintegration particles
            if (progress.remainingCraftingTime() < 10) {
                addDisintegrationParticles(level, random, worktable, progress);
            }
        }

        // Render items until the very end (they'll be too small then anyway
        if (progress.remainingCraftingTime() > 5) {
            levitateItems(worktable, progress.value(), pedestals, level, itemRenderer, partialTick, poseStack, bufferSource);
        }

        // Start to render the resulting item
        if (progress.remainingCraftingTime() < 30 && !progress.currentlyCrafted().isEmpty()) {
            var scale = getSymmetricVec3d(getCraftingResultScaleForProgress(progress.value()));
            RenderingUtil.levitateItemAboveBlock(level, worktable.pos, Vec3.ZERO, scale, partialTick, progress.currentlyCrafted(), itemRenderer, poseStack, bufferSource);
        }
    }

    protected static double getCraftingResultScaleForProgress(double p) {
        return -1 / (19 * p - 20);
    }

    protected static void levitateItems(RenderingData worktable, float craftingProgress, List<RenderingData> pedestals, Level level, ItemRenderer itemRenderer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        var scale = getSymmetricVec3d(1 - craftingProgress);
        RenderingUtil.levitateItemAboveBlock(level, worktable.pos, Vec3.ZERO, scale, partialTick, worktable.stack, itemRenderer, poseStack, bufferSource);

        for (var pedestal : pedestals) {
            var translation = worktable.above.vectorTo(pedestal.above);
            RenderingUtil.levitateItemAboveBlock(level, pedestal.pos, translation, scale, partialTick, pedestal.stack, itemRenderer, poseStack, bufferSource);
        }
    }

    // ALL, and I mean - ALL parenthesis matter here
    protected static double getVelocityForProgress(double p) {
        return 0.2 * (-0.4 - 1.05 / (1 * (1.1 * Math.log10(-2 * p + 2.0) + 1 * (2 * p - 2))));
    }

    protected static void addParticleBeam(RenderingData worktable, CraftingWorktableBlockEntity.Progress progress, RenderingData pedestal, Level level, float partialTick) {
        if (pedestal.stack.isEmpty()) return;

        if (level.random.nextFloat() < Wizcraft.getConfig().animationParticlesAmount.modifier) {
            var temporalOffset = Math.abs(Mth.sin((progress.remainingCraftingTime() + partialTick)));
            var path = pedestal.above.vectorTo(worktable.above);
            var pos = pedestal.above.add(path.scale(Math.min(0.6, temporalOffset * progress.value())));
            var velocity = path.normalize().scale(getVelocityForProgress(progress.value()));
            RenderingUtil.addParticle(level, pedestal.particleOptions, pos, velocity);
        }
    }

    // ALL, and I mean - ALL parenthesis matter here
    protected static double getRadiusForProgress(double p) {
        return 1.75 * (-4.3 / (3.6 * p - 6.35) - 1.85 * p + 0.3);
    }

    protected static void addParticleHurricane(RenderingData worktable, CraftingWorktableBlockEntity.Progress progress, RenderingData pedestal, Level level, float partialTick) {
        if (pedestal.stack.isEmpty()) return;

        var temporalOffset = Math.abs(Mth.sin((progress.remainingCraftingTime() + partialTick)));
        for (int i = 0; i < (1 - progress.value()) * 3 * Wizcraft.getConfig().animationParticlesAmount.modifier; i++) {
            var theta = 2f * Mth.PI * temporalOffset + i * temporalOffset;
            var r = getRadiusForProgress(progress.value());
            var hx = r * Mth.cos(theta);
            var hz = r * Mth.sin(theta);
            var pos = worktable.above.add(hx, 0, hz);
            // Vector tangent to a circle https://stackoverflow.com/q/40710168
            var velocity = new Vec3(hz, 0, -hx).normalize().scale(getVelocityForProgress(progress.value()));
            var randomizedPos = pos.add(
                    (hx / 16) * level.random.nextFloat(),
                    (level.random.nextGaussian() - 0.5) / 16,
                    (hz / 16) * level.random.nextFloat());
            RenderingUtil.addParticle(level, pedestal.particleOptions, randomizedPos, velocity);
        }
    }

    protected static void addDisintegrationParticles(Level level, RandomSource random, RenderingData renderingData, CraftingWorktableBlockEntity.Progress progress) {
        if (renderingData.stack.isEmpty()) return;

        var velocity = new Vec3((random.nextFloat() - 0.5) / 8, random.nextGaussian() / 16, (random.nextFloat() - 0.5) / 8);
        var amount = progress.value() * 10 * Wizcraft.getConfig().animationParticlesAmount.modifier * (renderingData.stack.getItem() instanceof BlockItem ? 3 : 1);
        for (int i = 0; i < amount; i++) {
            RenderingUtil.addParticle(level, renderingData.particleOptions, renderingData.above, velocity);
        }
    }

    @Override
    public void render(CraftingWorktableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var world = blockEntity.getLevel();
        if (world == null) return;

        var worktable = new RenderingData(blockEntity.getBlockPos(), blockEntity.getHeldStackCopy());
        var progress = blockEntity.getProgress();
        var pedestals = blockEntity.getNonEmptyPedestalPositions().stream()
                .map(world::getBlockEntity)
                .flatMap(it -> it instanceof LensingPedestalBlockEntity pedestal ? Stream.of(pedestal) : Stream.empty())
                .map(pedestal -> new RenderingData(pedestal.getBlockPos(), pedestal.getHeldStackCopy()))
                .toList();

        if (pedestals.isEmpty() && worktable.stack.isEmpty()) return;

        poseStack.pushPose();

        if (progress.remainingCraftingTime() > 0) {
            animateCraftingProgress(worktable, progress, pedestals, world, itemRenderer, partialTick, poseStack, bufferSource);
        } else {
            levitateItems(worktable, 0, pedestals, world, itemRenderer, partialTick, poseStack, bufferSource);
        }

        poseStack.popPose();
    }

    public record RenderingData(BlockPos pos, Vec3 center, Vec3 above, ItemStack stack,
                                @Nullable ParticleOptions particleOptions) {
        private RenderingData(BlockPos pos, ItemStack stack) {
            this(pos, pos.getCenter(), pos.getCenter().add(0, 0.75, 0), stack, stack.isEmpty() ? null : new ItemParticleOption(WizcraftParticleTypes.SPAGHETTIFICATION, stack));
        }
    }
}
