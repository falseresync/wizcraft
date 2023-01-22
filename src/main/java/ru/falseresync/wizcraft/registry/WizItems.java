package ru.falseresync.wizcraft.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.item.WandItem;

import static ru.falseresync.wizcraft.util.IdUtil.id;

public class WizItems {
    public static final Item MAGIC_CAULDRON;
    public static final Item WAND;

    static {
        MAGIC_CAULDRON = new BlockItem(WizBlocks.MAGIC_CAULDRON, new FabricItemSettings());
        WAND = new WandItem();
    }

    public static void register() {
        Registry.register(Registries.ITEM, id("magic_cauldron"), MAGIC_CAULDRON);
        Registry.register(Registries.ITEM, id("wand"), WAND);
    }
}
