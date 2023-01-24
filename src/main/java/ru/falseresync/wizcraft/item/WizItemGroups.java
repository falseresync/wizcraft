package ru.falseresync.wizcraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import ru.falseresync.wizcraft.block.WizBlocks;

import static ru.falseresync.wizcraft.util.IdUtil.id;

public class WizItemGroups {
    public static final ItemGroup GENERAL;

    static {
         GENERAL = FabricItemGroup.builder(id("item_group"))
                .entries((enabledFeatures, entries, operatorEnabled) -> {
                    entries.add(WizBlocks.MAGIC_CAULDRON);
                    entries.add(WizItems.WAND);
                })
                .build();
    }

    public static void init() {
    }
}
