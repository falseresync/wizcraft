package dev.falseresync.wizcraft.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.falseresync.wizcraft.common.Wizcraft;
import eu.midnightdust.lib.config.MidnightConfig;

public class WizcraftModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MidnightConfig.getScreen(parent, Wizcraft.MOD_ID);
    }
}
