package dev.falseresync.wizcraft.common.block;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBuilder;
import dev.falseresync.wizcraft.api.common.worktable.WorktableVariant;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.entity.WizcraftBlockEntities;
import dev.falseresync.wizcraft.common.block.entity.worktable.ChargingWorktableBlockEntity;
import dev.falseresync.wizcraft.common.block.entity.worktable.CraftingWorktableBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizcraftBlocks {
    public static final WizBlock LENS;
    public static final DummyWorktableBlock DUMMY_WORKTABLE;
    public static final WorktableBlock<CraftingWorktableBlockEntity> CRAFTING_WORKTABLE;
    public static final WorktableBlock<ChargingWorktableBlockEntity> CHARGING_WORKTABLE;
    public static final LensingPedestalBlock LENSING_PEDESTAL;
    private static final Map<Identifier, Block> TO_REGISTER = new HashMap<>();

    static {
        LENS = r(new WizBlock(new Identifier(Wizcraft.MODID, "lens"), FabricBlockSettings.copyOf(Blocks.GLASS).luminance(1)));

        var worktableBlockSettings = FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE).requiresTool();
        DUMMY_WORKTABLE = r(new DummyWorktableBlock(worktableBlockSettings));

        CRAFTING_WORKTABLE = rWorktable(new WorktableBuilder<CraftingWorktableBlockEntity>()
                .id(new Identifier(Wizcraft.MODID, "crafting_worktable"))
                .settings(worktableBlockSettings)
                .type(() -> WizcraftBlockEntities.CRAFTING_WORKTABLE)
                .ticker(CraftingWorktableBlockEntity::tick)
                .pattern(WizcraftWorktablePatterns::crafting)
                .build());
        CHARGING_WORKTABLE = rWorktable(new WorktableBuilder<ChargingWorktableBlockEntity>()
                .id(new Identifier(Wizcraft.MODID, "crafting_worktable"))
                .settings(worktableBlockSettings)
                .type(() -> WizcraftBlockEntities.CHARGING_WORKTABLE)
                .ticker(ChargingWorktableBlockEntity::tick)
                .pattern(WizcraftWorktablePatterns::charging)
                .build());

        LENSING_PEDESTAL = r(new LensingPedestalBlock(FabricBlockSettings.copyOf(Blocks.BRICK_WALL)));
    }

    private static <T extends WorktableBlock<?>> T rWorktable(T block) {
        TO_REGISTER.put(block.getId(), block);
        WorktableVariant.register(block.getVariant());
        return block;
    }

    private static <T extends Block & HasId> T r(T block) {
        TO_REGISTER.put(block.getId(), block);
        return block;
    }

    public static void register(BiConsumer<Identifier, Block> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
