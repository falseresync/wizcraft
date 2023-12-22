package dev.falseresync.common;

import dev.falseresync.api.WizRegistries;
import dev.falseresync.common.entity.WizEntityTypes;
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
        WizRegistries.register();
        WizItems.registerItems((id, item) -> Registry.register(Registries.ITEM, id, item));
        WizItems.registerItemGroups((id, item) -> Registry.register(Registries.ITEM_GROUP, id, item));
        WizEntityTypes.register((id, entityType) -> Registry.register(Registries.ENTITY_TYPE, id, entityType));
    }
}