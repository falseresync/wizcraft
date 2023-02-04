package ru.falseresync.wizcraft.common.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;

import static ru.falseresync.wizcraft.common.IdUtil.id;

public class WizItemGroups {
    public static final ItemGroup GENERAL = FabricItemGroup.builder(id("item_group"))
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                entries.add(WizBlocks.MAGIC_CAULDRON);
                entries.add(WizItems.WAND);
            })
            .build();

    public static void init() {
    }
}
