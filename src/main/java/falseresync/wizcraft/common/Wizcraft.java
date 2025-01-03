package falseresync.wizcraft.common;

import falseresync.lib.registry.AutoRegistry;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.focus.Focus;
import falseresync.wizcraft.common.focus.WizcraftFocuses;
import falseresync.wizcraft.common.item.WizcraftItemGroups;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MOD_ID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft");

    @Override
    public void onInitialize() {
        WizcraftItems.init();
        new AutoRegistry(MOD_ID, LOGGER)
                .link(Focus.REGISTRY, WizcraftFocuses.class)
                .link(Registries.ITEM_GROUP, WizcraftItemGroups.class)
                .link(Registries.DATA_COMPONENT_TYPE, WizcraftDataComponents.class);
    }

    public static Identifier id(String id) {
        return Identifier.of(id);
    }

    public static Identifier wid(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
