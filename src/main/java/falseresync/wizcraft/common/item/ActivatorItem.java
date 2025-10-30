package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.block.WizcraftBlockTags;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.block.WorktableVariant;
import falseresync.wizcraft.networking.s2c.TriggerBlockPatternTipS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Comparator;

public interface ActivatorItem {
    Object2ObjectMap<Block, ActivationBehavior> ANY_BEHAVIORS = new Object2ObjectArrayMap<>();
    Object2ObjectMap<Block, ActivationBehavior> WAND_BEHAVIORS = new Object2ObjectArrayMap<>();

    static void registerBehaviors() {
        ANY_BEHAVIORS.put(Blocks.WATER_CAULDRON, context -> {
            var player = context.getPlayer();
            if (player == null || player.isSpectator()) return InteractionResult.PASS;

            var world = context.getLevel();
            var pos = context.getClickedPos();
            if (world.getBlockState(pos.below()).is(WizcraftBlockTags.CRUCIBLE_HEAT_SOURCES)) {
                if (world.isClientSide) return InteractionResult.CONSUME;

                world.setBlock(pos, WizcraftBlocks.CRUCIBLE.defaultBlockState(), Block.UPDATE_ALL);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        });

        WAND_BEHAVIORS.putAll(ANY_BEHAVIORS);
        WAND_BEHAVIORS.put(WizcraftBlocks.DUMMY_WORKTABLE, context -> {
            var player = context.getPlayer();
            if (player == null || player.isSpectator()) return InteractionResult.PASS;

            var world = context.getLevel();
            if (world.isClientSide) return InteractionResult.CONSUME;

            var pos = context.getClickedPos();
            var serverPlayer = (ServerPlayer) player;
            var matchedVariants = WorktableVariant.getForPlayerAndSearchAround((ServerLevel) world, serverPlayer, pos);
            var fullyCompletedVariant = matchedVariants.stream()
                    .filter(variant -> variant.match().isCompleted())
                    .findFirst();
            if (fullyCompletedVariant.isPresent()) {
                world.setBlockAndUpdate(pos, fullyCompletedVariant.get().block().defaultBlockState());
                return InteractionResult.SUCCESS;
            }

            var leastUncompletedVariant = matchedVariants.stream()
                    .filter(variant -> variant.match().isHalfwayCompleted())
                    .max(Comparator.comparingInt(variant -> variant.match().size()));
//                    .min(Comparator.comparingInt(variant -> variant.match().delta().size()));
            if (leastUncompletedVariant.isPresent()) {
                ServerPlayNetworking.send(serverPlayer, new TriggerBlockPatternTipS2CPayload(leastUncompletedVariant.get().match().deltaAsBlockPos()));
                player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1f, 1f);
                player.displayClientMessage(Component.translatable("hud.wizcraft.worktable.incomplete_worktable"), true);

                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });
    }

    default InteractionResult activateBlock(Object2ObjectMap<Block, ActivationBehavior> behaviors, UseOnContext context) {
        var world = context.getLevel();
        var pos = context.getClickedPos();
        var state = world.getBlockState(pos);
        var behavior = behaviors.get(state.getBlock());
        if (behavior != null) {
            return behavior.activateBlock(context);
        }

        return InteractionResult.PASS;
    }

    @FunctionalInterface
    interface ActivationBehavior {
        InteractionResult activateBlock(UseOnContext context);
    }
}
