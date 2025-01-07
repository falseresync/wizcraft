package falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DummyWorktableBlock extends Block {
    public static final String TRANSLATION_KEY = "block.wizcraft.worktable";
    public static final MapCodec<DummyWorktableBlock> CODEC = createCodec(DummyWorktableBlock::new);

    public DummyWorktableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<DummyWorktableBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return WorktableBlock.SHAPE;
    }

    @Override
    public String getTranslationKey() {
        return TRANSLATION_KEY;
    }
}