package dev.falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.HasId;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DummyWorktableBlock extends Block implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "worktable");
    public static final MapCodec<DummyWorktableBlock> CODEC = createCodec(DummyWorktableBlock::new);

    public DummyWorktableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<DummyWorktableBlock> getCodec() {
        return CODEC;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return WorktableBlock.SHAPE;
    }
}
