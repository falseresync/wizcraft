package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.block.WizcraftBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;


public interface ActivatorItem {
    Object2ObjectMap<Block, ActivationBehavior> BEHAVIORS = new Object2ObjectArrayMap<>();

    default ActionResult activateBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var state = world.getBlockState(pos);
        var behavior = BEHAVIORS.get(state.getBlock());
        if (behavior != null) {
            return behavior.activateBlock(context);
        }

        return ActionResult.PASS;
    }

    static void registerBehaviors() {
        BEHAVIORS.put(Blocks.WATER_CAULDRON, context -> {
            var world = context.getWorld();
            var player = context.getPlayer();
            if (player == null || player.isSpectator()) return ActionResult.PASS;

            var pos = context.getBlockPos();
            if (world.getBlockState(pos.down()).isOf(Blocks.FIRE)) {
                if (world.isClient) return ActionResult.CONSUME;

                world.setBlockState(pos, WizcraftBlocks.CRUCIBLE.getDefaultState(), Block.NOTIFY_ALL);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        });
    }

    @FunctionalInterface
    interface ActivationBehavior {
        ActionResult activateBlock(ItemUsageContext context);
    }
}
