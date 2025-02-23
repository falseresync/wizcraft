package falseresync.wizcraft.common.block;

import com.mojang.serialization.*;
import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.blockentity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class LensingPedestalBlock extends BlockWithEntity {
    public static final MapCodec<LensingPedestalBlock> CODEC = createCodec(LensingPedestalBlock::new);
    public static final VoxelShape SHAPE = createCuboidShape(4, 0, 4, 12, 16, 12);

    protected LensingPedestalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<LensingPedestalBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LensingPedestalBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof LensingPedestalBlockEntity pedestal) {
            if (world.isClient()) {
                return ItemActionResult.SUCCESS;
            }

            var exchanged = WizcraftUtil.exchangeStackInSlotWithHand(player, hand, pedestal.getStorage(), 0, 1, null);
            if (exchanged == 1) {
                return ItemActionResult.CONSUME;
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof LensingPedestalBlockEntity pedestal) {
                ItemScatterer.spawn(world, pos, pedestal.getInventory());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}