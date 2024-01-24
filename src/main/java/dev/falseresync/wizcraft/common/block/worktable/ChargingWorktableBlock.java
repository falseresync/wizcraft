package dev.falseresync.wizcraft.common.block.worktable;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.block.entity.worktable.ChargingWorktableBlockEntity;
import dev.falseresync.wizcraft.common.block.pattern.BetterBlockPattern;
import dev.falseresync.wizcraft.common.block.pattern.BetterBlockPatternBuilder;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ChargingWorktableBlock extends WorktableBlock<ChargingWorktableBlockEntity> {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "charging_worktable");
    public static final MapCodec<ChargingWorktableBlock> CODEC = createCodec(ChargingWorktableBlock::new);
    private static BetterBlockPattern PATTERN;

    public ChargingWorktableBlock(Settings settings) {
        super(() -> WizBlockEntities.CHARGING_WORKTABLE, ChargingWorktableBlockEntity::tick, ChargingWorktableBlock::getPattern, settings);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected MapCodec<ChargingWorktableBlock> getCodec() {
        return CODEC;
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
