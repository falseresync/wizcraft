package falseresync.wizcraft.common.block;

import com.mojang.serialization.*;
import falseresync.wizcraft.common.blockentity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class LensBlock extends BlockWithEntity {
    public static final MapCodec<LensBlock> CODEC = createCodec(LensBlock::new);
    public static final VoxelShape SHAPE = createCuboidShape(3, 1, 3, 13, 15, 13);

    public LensBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<LensBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LensBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
