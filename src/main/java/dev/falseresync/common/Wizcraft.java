package dev.falseresync.common;

import dev.falseresync.common.item.WizItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MODID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("wizcraft");

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

//        var wand = new SkyWandItem(new FabricItemSettings().maxCount(1));
//        Registry.register(Registries.ITEM, new Identifier("wizcraft:sky_wand"), wand);
        WizItems.registerItems((id, item) -> Registry.register(Registries.ITEM, id, item));
        WizItems.registerItemGroups((id, item) -> Registry.register(Registries.ITEM_GROUP, id, item));
    }
}