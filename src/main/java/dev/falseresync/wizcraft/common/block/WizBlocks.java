package dev.falseresync.wizcraft.common.block;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.HasId;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizBlocks {
    public static final WizBlock LENS;
    public static final PlatedWorktableBlock PLATED_WORKTABLE;
    public static final LensingPedestalBlock LENSING_PEDESTAL;
    private static final Map<Identifier, Block> TO_REGISTER = new HashMap<>();

    static {
        LENS = r(new WizBlock(new Identifier(Wizcraft.MODID, "lens"), FabricBlockSettings.copyOf(Blocks.GLASS).luminance(1)));
        PLATED_WORKTABLE = r(new PlatedWorktableBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE)));
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
