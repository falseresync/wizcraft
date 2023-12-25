package dev.falseresync.wizcraft.common;

import dev.falseresync.wizcraft.common.entity.WizEntityTypes;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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
        WizFocuses.register((id, focus) -> Registry.register(WizRegistries.FOCUSES, id, focus));
    }
}