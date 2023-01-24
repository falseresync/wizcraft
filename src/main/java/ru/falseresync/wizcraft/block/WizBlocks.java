package ru.falseresync.wizcraft.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.block.MagicCauldronBlock;

import static ru.falseresync.wizcraft.util.IdUtil.id;

public class WizBlocks {
    public static final MagicCauldronBlock MAGIC_CAULDRON;

    static {
        MAGIC_CAULDRON = new MagicCauldronBlock();
    }

    public static void register() {
        Registry.register(Registries.BLOCK, id("magic_cauldron"), MAGIC_CAULDRON);
    }
}
