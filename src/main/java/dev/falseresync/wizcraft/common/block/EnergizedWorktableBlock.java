package dev.falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.lib.HasId;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EnergizedWorktableBlock extends BlockWithEntity implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "energized_worktable");

    protected EnergizedWorktableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
