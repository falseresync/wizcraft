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
    public static final Block LENS;
    public static final EnergizedWorktableBlock ENERGIZED_WORKTABLE;
    public static final LensingPedestalBlock LENSING_PEDESTAL;
    private static final Map<Identifier, Block> TO_REGISTER = new HashMap<>();

    static {
        LENS = r("lens", new Block(FabricBlockSettings.copyOf(Blocks.GLASS).luminance(1)));
        ENERGIZED_WORKTABLE = r(new EnergizedWorktableBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE).requiresTool()));
        LENSING_PEDESTAL = r(new LensingPedestalBlock(FabricBlockSettings.copyOf(Blocks.BRICK_WALL)));
    }

    private static <T extends Block> T r(String id, T block) {
        TO_REGISTER.put(new Identifier(Wizcraft.MODID, id), block);
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
