package falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.wizcraft.common.blockentity.LensBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;

public class LensBlock extends BaseEntityBlock {
    public static final MapCodec<LensBlock> CODEC = simpleCodec(LensBlock::new);
    public static final VoxelShape SHAPE = box(3, 1, 3, 13, 15, 13);

    public LensBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<LensBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LensBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
