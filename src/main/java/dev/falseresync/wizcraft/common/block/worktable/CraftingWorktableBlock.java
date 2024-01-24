package dev.falseresync.wizcraft.common.block.worktable;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.block.entity.worktable.CraftingWorktableBlockEntity;
import dev.falseresync.wizcraft.common.block.pattern.BetterBlockPattern;
import dev.falseresync.wizcraft.common.block.pattern.BetterBlockPatternBuilder;
import net.minecraft.util.Identifier;

public class CraftingWorktableBlock extends WorktableBlock<CraftingWorktableBlockEntity> {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "crafting_worktable");
    public static final MapCodec<CraftingWorktableBlock> CODEC = createCodec(CraftingWorktableBlock::new);
    private static BetterBlockPattern PATTERN;

    public CraftingWorktableBlock(Settings settings) {
        super(() -> WizBlockEntities.CRAFTING_WORKTABLE, CraftingWorktableBlockEntity::tick, CraftingWorktableBlock::getPattern, settings);
    }

    @Override
    protected MapCodec<CraftingWorktableBlock> getCodec() {
        return CODEC;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    protected static BetterBlockPattern getPattern() {
        if (PATTERN == null) {
            PATTERN = new BetterBlockPatternBuilder()
                    .layer(" ... ", ".....", ".....", ".....", " ... ")
                    .layer(" .p. ", ".....", "p.w.p", ".....", " .p. ")
                    .where('.', pos -> pos.getBlockState().isAir())
                    .where('w', pos -> pos.getBlockState().isOf(WizBlocks.DUMMY_WORKTABLE))
                    .where('p', pos -> pos.getBlockState().isOf(WizBlocks.LENSING_PEDESTAL))
                    .preserveUp()
                    .build();
        }
        return PATTERN;
    }
}
