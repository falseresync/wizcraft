package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.block.WizcraftBlockTags;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.block.WorktableVariant;
import falseresync.wizcraft.networking.report.WizcraftReports;
import falseresync.wizcraft.networking.s2c.TriggerBlockPatternTipS2CPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import java.util.Comparator;

public interface ActivatorItem {
    Object2ObjectMap<Block, ActivationBehavior> ANY_BEHAVIORS = new Object2ObjectArrayMap<>();
    Object2ObjectMap<Block, ActivationBehavior> WAND_BEHAVIORS = new Object2ObjectArrayMap<>();

    default ActionResult activateBlock(Object2ObjectMap<Block, ActivationBehavior> behaviors, ItemUsageContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var state = world.getBlockState(pos);
        var behavior = behaviors.get(state.getBlock());
        if (behavior != null) {
            return behavior.activateBlock(context);
        }

        return ActionResult.PASS;
    }

    static void registerBehaviors() {
        ANY_BEHAVIORS.put(Blocks.WATER_CAULDRON, context -> {
            var player = context.getPlayer();
            if (player == null || player.isSpectator()) return ActionResult.PASS;

            var world = context.getWorld();
            var pos = context.getBlockPos();
            if (world.getBlockState(pos.down()).isIn(WizcraftBlockTags.CRUCIBLE_HEAT_SOURCES)) {
                if (world.isClient) return ActionResult.CONSUME;

                world.setBlockState(pos, WizcraftBlocks.CRUCIBLE.getDefaultState(), Block.NOTIFY_ALL);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        });

        WAND_BEHAVIORS.putAll(ANY_BEHAVIORS);
        WAND_BEHAVIORS.put(WizcraftBlocks.DUMMY_WORKTABLE, context -> {
            var player = context.getPlayer();
            if (player == null || player.isSpectator()) return ActionResult.PASS;

            var world = context.getWorld();
            if (world.isClient) return ActionResult.CONSUME;

            var pos = context.getBlockPos();
            var serverPlayer = (ServerPlayerEntity) player;
            var matchedVariants = WorktableVariant.getForPlayerAndSearchAround((ServerWorld) world, serverPlayer, pos);
            var fullyCompletedVariant = matchedVariants.stream()
                    .filter(variant -> variant.match().isCompleted())
                    .findFirst();
            if (fullyCompletedVariant.isPresent()) {
                world.setBlockState(pos, fullyCompletedVariant.get().block().getDefaultState());
                return ActionResult.SUCCESS;
            }

            var leastUncompletedVariant = matchedVariants.stream()
                    .filter(variant -> variant.match().isHalfwayCompleted())
                    .max(Comparator.comparingInt(variant -> variant.match().size()));
//                    .min(Comparator.comparingInt(variant -> variant.match().delta().size()));
            if (leastUncompletedVariant.isPresent()) {
                ServerPlayNetworking.send(serverPlayer, new TriggerBlockPatternTipS2CPacket(leastUncompletedVariant.get().match().deltaAsBlockPos()));
                WizcraftReports.WORKTABLE_INCOMPLETE.sendTo(serverPlayer);

                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }

    @FunctionalInterface
    interface ActivationBehavior {
        ActionResult activateBlock(ItemUsageContext context);
    }
}
