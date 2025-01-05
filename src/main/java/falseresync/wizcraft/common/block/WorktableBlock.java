package falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.blockentity.WorktableBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class WorktableBlock<B extends WorktableBlockEntity> extends BlockWithEntity {
    public static final VoxelShape SHAPE = VoxelShapes.union(
            /* Panel */ VoxelShapes.cuboid(0, 14 / 16f, 0, 16 / 16f, 16 / 16f, 16 / 16f),
            /* Base */ VoxelShapes.cuboid(2 / 16f, 0, 2 / 16f, 14 / 16f, 4 / 16f, 14 / 16f),
            /* Post */ VoxelShapes.cuboid(4 / 16f, 4 / 16f, 4 / 16f, 12 / 16f, 14 / 16f, 12 / 16f)
    );

    public WorktableBlock(Settings settings) {
        super(settings);
    }

    // Dear vanilla, suck dick for Codecs. Those are really the epitome of PITA
    // But noooooooo, it MUUUUUST be data-driven, ffs
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public String getTranslationKey() {
        return WizcraftBlocks.DUMMY_WORKTABLE.getTranslationKey();
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(WizcraftItems.WORKTABLE);
    }

    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    @Override
    public abstract  <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type);

    public abstract WorktableVariant<B> getVariant();

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof WorktableBlockEntity worktable) {
            if (world.isClient()) return ItemActionResult.CONSUME;

            if (worktable.shouldExchangeFor(stack)) {
                var exchanged = Wizcraft.exchangeStackInSlotWithHand(player, hand, worktable.getStorage(), 0, 1, null);
                if (exchanged == 1) {
                    return ItemActionResult.CONSUME;
                }
            }

            if (worktable.canBeActivatedBy(stack)) {
                worktable.activate(player);
                return ItemActionResult.SUCCESS;
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
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
