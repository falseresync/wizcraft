package dev.falseresync.wizcraft.common.block.worktable;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.HasId;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

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
}
