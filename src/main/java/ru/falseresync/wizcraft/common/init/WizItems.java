package ru.falseresync.wizcraft.common.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import ru.falseresync.wizcraft.common.item.WandItem;
import ru.falseresync.wizcraft.lib.registry.RegistryObject;

public class WizItems {
    public static final @RegistryObject Item MAGIC_CAULDRON = new BlockItem(WizBlocks.MAGIC_CAULDRON, new FabricItemSettings());
    public static final @RegistryObject Item WAND = new WandItem();
}
