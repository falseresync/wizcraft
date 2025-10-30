package falseresync.wizcraft.common.block;

import falseresync.lib.blockpattern.BetterBlockPattern;
import falseresync.lib.blockpattern.BetterBlockPatternBuilder;

public class WorktablePatterns {
    public static BetterBlockPattern crafting() {
        return new BetterBlockPatternBuilder()
                .layer(" ... ", ".....", ".....", ".....", " ... ")
                .layer(" .p. ", ".....", "p.w.p", ".....", " .p. ")
                .where('.', pos -> pos.getState().isAir())
                .where('w', pos -> pos.getState().is(WizcraftBlocks.DUMMY_WORKTABLE))
                .where('p', pos -> pos.getState().is(WizcraftBlocks.LENSING_PEDESTAL))
                .preserveUp()
                .build();
    }

    public static BetterBlockPattern charging() {
        return new BetterBlockPatternBuilder()
                .sidewaysLayers()
                .layer(".", "l", ".", "w")
                .where('.', pos -> pos.getState().isAir())
                .where('l', pos -> pos.getState().is(WizcraftBlocks.LENS))
                .where('w', pos -> pos.getState().is(WizcraftBlocks.DUMMY_WORKTABLE))
                .preserveUp()
                .build();
    }
}