package dev.falseresync.wizcraft.common.block;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.block.worktable.ChargingWorktableBlock;
import dev.falseresync.wizcraft.common.block.worktable.CraftingWorktableBlock;
import dev.falseresync.wizcraft.common.block.worktable.DummyWorktableBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizBlocks {
    public static final WizBlock LENS;
    public static final DummyWorktableBlock DUMMY_WORKTABLE;
    public static final CraftingWorktableBlock CRAFTING_WORKTABLE;
    public static final ChargingWorktableBlock CHARGING_WORKTABLE;
    public static final LensingPedestalBlock LENSING_PEDESTAL;
    private static final Map<Identifier, Block> TO_REGISTER = new HashMap<>();

    static {
        LENS = r(new WizBlock(new Identifier(Wizcraft.MODID, "lens"), FabricBlockSettings.copyOf(Blocks.GLASS).luminance(1)));
        DUMMY_WORKTABLE = r(new DummyWorktableBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE).requiresTool()));
        CRAFTING_WORKTABLE = r(new CraftingWorktableBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE).requiresTool()));
        CHARGING_WORKTABLE = r(new ChargingWorktableBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE).requiresTool()));
        LENSING_PEDESTAL = r(new LensingPedestalBlock(FabricBlockSettings.copyOf(Blocks.BRICK_WALL)));
    }

    private static <T extends Block & HasId> T r(T block) {
        TO_REGISTER.put(block.getId(), block);
        return block;
    }

    public static void register(BiConsumer<Identifier, Block> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
