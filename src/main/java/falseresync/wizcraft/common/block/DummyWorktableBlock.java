package falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DummyWorktableBlock extends Block {
    public static final String TRANSLATION_KEY = "block.wizcraft.worktable";
    public static final MapCodec<DummyWorktableBlock> CODEC = simpleCodec(DummyWorktableBlock::new);

    public DummyWorktableBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<DummyWorktableBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return WorktableBlock.SHAPE;
    }

    @Override
    public String getDescriptionId() {
        return TRANSLATION_KEY;
    }
}