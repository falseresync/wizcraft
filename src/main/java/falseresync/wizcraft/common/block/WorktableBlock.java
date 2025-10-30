package falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.wizcraft.common.WizcraftUtil;
import falseresync.wizcraft.common.blockentity.WorktableBlockEntity;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class WorktableBlock<B extends WorktableBlockEntity> extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Shapes.or(
            /* Panel */ Shapes.box(0, 14 / 16f, 0, 16 / 16f, 16 / 16f, 16 / 16f),
            /* Base */ Shapes.box(2 / 16f, 0, 2 / 16f, 14 / 16f, 4 / 16f, 14 / 16f),
            /* Post */ Shapes.box(4 / 16f, 4 / 16f, 4 / 16f, 12 / 16f, 14 / 16f, 12 / 16f)
    );

    public WorktableBlock(Properties settings) {
        super(settings);
    }

    // Dear vanilla, suck dick for Codecs. Those are really the epitome of PITA
    // But noooooooo, it MUUUUUST be data-driven, ffs
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public String getDescriptionId() {
        return WizcraftBlocks.DUMMY_WORKTABLE.getDescriptionId();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
        return new ItemStack(WizcraftItems.WORKTABLE);
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type);

    public abstract WorktableVariant<B> getVariant();

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof WorktableBlockEntity worktable) {
            if (world.isClientSide()) return ItemInteractionResult.CONSUME;

            if (worktable.shouldExchangeFor(stack)) {
                var exchanged = WizcraftUtil.exchangeStackInSlotWithHand(player, hand, worktable.getStorage(), 0, 1, null);
                if (exchanged == 1) {
                    return ItemInteractionResult.CONSUME;
                }
            }

            if (worktable.canBeActivatedBy(stack)) {
                worktable.activate(player);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof WorktableBlockEntity worktable) {
                worktable.remove(world, pos);
                Containers.dropContents(world, pos, worktable.getInventory());
            }

            super.onRemove(state, world, pos, newState, moved);
        }
    }
}
