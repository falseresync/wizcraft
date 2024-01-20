package dev.falseresync.wizcraft.api.common.worktable;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.WizUtil;
import dev.falseresync.wizcraft.common.item.WizItems;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Dear vanilla, suck dick for Codecs. Those are really the epitome of PITA
// This one, for example, has prevented me from completely generifying this class
// I could've had a Builder here that takes my BE params and spits out a block automatically
// But noooooooo, it MUUUUUST be data-driven, ffs
public abstract class WorktableBlock<B extends WorktableBlockEntity> extends BlockWithEntity implements HasId {
    protected final Supplier<BlockEntityType<B>> type;
    protected final BlockEntityTicker<B> ticker;

    public WorktableBlock(Supplier<BlockEntityType<B>> type, BlockEntityTicker<B> ticker, Settings settings) {
        super(settings);
        this.type = type;
        this.ticker = ticker;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return type.get().instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, this.type.get(), ticker);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof WorktableBlockEntity worktable) {
            if (world.isClient()) return ActionResult.CONSUME;

            var playerStack = player.getMainHandStack();
            if (playerStack.isOf(WizItems.WAND)) {
                worktable.interact(player);
                return ActionResult.SUCCESS;
            }

            var exchanged = WizUtil.exchangeStackInSlotWithHand(player, hand, worktable.getStorage(), 0, 1, null);
            if (exchanged == 1) {
                return ActionResult.CONSUME;
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof WorktableBlockEntity worktable) {
                worktable.remove(world, pos);
                ItemScatterer.spawn(world, pos, worktable.getInventory());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
