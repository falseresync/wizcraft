package dev.falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.entity.LensingPedestalBlockEntity;
import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.CommonUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LensingPedestalBlock extends BlockWithEntity implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MOD_ID, "lensing_pedestal");
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof LensingPedestalBlockEntity pedestal) {
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            var exchanged = CommonUtils.exchangeStackInSlotWithHand(player, hand, pedestal.getStorage(), 0, 1, null);
            if (exchanged == 1) {
                return ActionResult.CONSUME;
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
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

    @Override
    public Identifier getId() {
        return ID;
    }
}
