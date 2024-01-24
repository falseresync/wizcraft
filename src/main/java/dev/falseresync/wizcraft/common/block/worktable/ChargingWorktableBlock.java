package dev.falseresync.wizcraft.common.block.worktable;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.block.entity.worktable.ChargingWorktableBlockEntity;
import dev.falseresync.wizcraft.common.block.pattern.BetterBlockPattern;
import dev.falseresync.wizcraft.common.block.pattern.BetterBlockPatternBuilder;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ChargingWorktableBlock extends WorktableBlock<ChargingWorktableBlockEntity> {
    private static BetterBlockPattern PATTERN;

    public ChargingWorktableBlock(Supplier<BlockEntityType<ChargingWorktableBlockEntity>> type, BlockEntityTicker<ChargingWorktableBlockEntity> ticker, Settings settings) {
        super(type, ticker, ChargingWorktableBlock::getPattern, settings);
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    protected MapCodec<ChargingWorktableBlock> getCodec() {
        return null;
    }

    public static BetterBlockPattern getPattern() {
        if (PATTERN == null) {
            PATTERN = new BetterBlockPatternBuilder()
                    .sidewaysLayers()
                    .layer(".", "l", ".", "w")
                    .where('.', pos -> pos.getBlockState().isAir())
                    .where('l', pos -> pos.getBlockState().isOf(WizBlocks.LENS))
                    .where('w', pos -> pos.getBlockState().isOf(WizBlocks.DUMMY_WORKTABLE))
                    .preserveUp()
                    .build();
        }
        return PATTERN;
    }
}
