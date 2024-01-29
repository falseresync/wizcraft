package dev.falseresync.wizcraft.common.block;

import dev.falseresync.wizcraft.api.common.blockpattern.BetterBlockPattern;
import dev.falseresync.wizcraft.api.common.blockpattern.BetterBlockPatternBuilder;

public class WizcraftWorktablePatterns {
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
