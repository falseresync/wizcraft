package dev.falseresync.wizcraft.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.falseresync.wizcraft.common.WizcraftConfig;

public class WizcraftModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> WizcraftConfig.HANDLER.generateGui().generateScreen(parent);
    }
}
