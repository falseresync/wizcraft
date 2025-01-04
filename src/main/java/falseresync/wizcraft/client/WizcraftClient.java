package falseresync.wizcraft.client;

import falseresync.wizcraft.client.render.WizcraftRenderers;
import net.fabricmc.api.ClientModInitializer;

public class WizcraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WizcraftRenderers.init();
    }
}
