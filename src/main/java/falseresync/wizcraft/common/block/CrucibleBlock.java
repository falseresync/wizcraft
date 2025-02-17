package falseresync.wizcraft.common.block;

import com.mojang.serialization.*;
import falseresync.wizcraft.common.blockentity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.function.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class CrucibleBlock extends BlockWithEntity {
    public static final MapCodec<CrucibleBlock> CODEC = createCodec(CrucibleBlock::new);
    private static final VoxelShape RAYCAST_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
            VoxelShapes.fullCube(),
            VoxelShapes.union(
                    createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                    createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                    createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                    RAYCAST_SHAPE
            ),
            BooleanBiFunction.ONLY_FIRST
    );

    public CrucibleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<CrucibleBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, WizcraftBlockEntities.CRUCIBLE, CrucibleBlockEntity::tick);
    }


    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
