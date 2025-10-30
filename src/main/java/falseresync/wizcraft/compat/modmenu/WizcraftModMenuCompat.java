package falseresync.wizcraft.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import falseresync.wizcraft.common.config.WizcraftConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class WizcraftModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(WizcraftConfig.class, parent).get();
    }
}
