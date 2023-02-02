package ru.falseresync.wizcraft.common.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.common.block.MagicCauldronBlock;

import static ru.falseresync.wizcraft.lib.IdUtil.id;

public class WizBlocks {
    public static final MagicCauldronBlock MAGIC_CAULDRON;

    static {
        MAGIC_CAULDRON = new MagicCauldronBlock();
    }

    public static void register() {
        Registry.register(Registries.BLOCK, id("magic_cauldron"), MAGIC_CAULDRON);
    }
}
