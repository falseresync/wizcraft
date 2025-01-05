package falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.blockentity.LensingPedestalBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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

            var exchanged = Wizcraft.exchangeStackInSlotWithHand(player, hand, pedestal.getStorage(), 0, 1, null);
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