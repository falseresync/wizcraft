package falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.wizcraft.common.WizcraftUtil;
import falseresync.wizcraft.common.blockentity.LensingPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LensingPedestalBlock extends BaseEntityBlock {
    public static final MapCodec<LensingPedestalBlock> CODEC = simpleCodec(LensingPedestalBlock::new);
    public static final VoxelShape SHAPE = box(4, 0, 4, 12, 16, 12);

    protected LensingPedestalBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<LensingPedestalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LensingPedestalBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof LensingPedestalBlockEntity pedestal) {
            if (world.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }

            var exchanged = WizcraftUtil.exchangeStackInSlotWithHand(player, hand, pedestal.getStorage(), 0, 1, null);
            if (exchanged == 1) {
                return ItemInteractionResult.CONSUME;
            }
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof LensingPedestalBlockEntity pedestal) {
                Containers.dropContents(world, pos, pedestal.getInventory());
            }

            super.onRemove(state, world, pos, newState, moved);
        }
    }
}