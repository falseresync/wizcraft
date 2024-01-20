package dev.falseresync.wizcraft.common.block.worktable;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.block.entity.worktable.CraftingWorktableBlockEntity;
import net.minecraft.util.Identifier;

public class CraftingWorktableBlock extends WorktableBlock<CraftingWorktableBlockEntity> {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "crafting_worktable");
    public static final MapCodec<CraftingWorktableBlock> CODEC = createCodec(CraftingWorktableBlock::new);

    public CraftingWorktableBlock(Settings settings) {
        super(() -> WizBlockEntities.CRAFTING_WORKTABLE, CraftingWorktableBlockEntity::tick, settings);
    }

    @Override
    protected MapCodec<CraftingWorktableBlock> getCodec() {
        return CODEC;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
