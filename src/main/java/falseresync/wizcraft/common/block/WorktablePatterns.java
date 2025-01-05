package falseresync.wizcraft.common.block;

import falseresync.lib.blockpattern.BetterBlockPattern;
import falseresync.lib.blockpattern.BetterBlockPatternBuilder;

public class WorktablePatterns {
    public static BetterBlockPattern crafting() {
        return new BetterBlockPatternBuilder()
                .layer(" ... ", ".....", ".....", ".....", " ... ")
                .layer(" .p. ", ".....", "p.w.p", ".....", " .p. ")
                .where('.', pos -> pos.getBlockState().isAir())
                .where('w', pos -> pos.getBlockState().isOf(WizcraftBlocks.DUMMY_WORKTABLE))
                .where('p', pos -> pos.getBlockState().isOf(WizcraftBlocks.LENSING_PEDESTAL))
                .preserveUp()
                .build();
    }

    public static BetterBlockPattern charging() {
        return new BetterBlockPatternBuilder()
                .sidewaysLayers()
                .layer(".", "l", ".", "w")
                .where('.', pos -> pos.getBlockState().isAir())
                .where('l', pos -> pos.getBlockState().isOf(WizcraftBlocks.LENS))
                .where('w', pos -> pos.getBlockState().isOf(WizcraftBlocks.DUMMY_WORKTABLE))
                .preserveUp()
                .build();
    }
}