package falseresync.wizcraft.client;

import falseresync.wizcraft.client.gui.WizcraftGui;
import falseresync.wizcraft.client.hud.WizcraftHud;
import falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import falseresync.wizcraft.client.render.WizcraftRenderers;
import falseresync.wizcraft.networking.WizcraftNetworkingClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class WizcraftClient implements ClientModInitializer {
    private static WizcraftHud hud;
    private static ToolManager toolManager;

    public static WizcraftHud getHud() {
        return hud;
    }

    public static ToolManager getToolManager() {
        return toolManager;
    }

    @Override
    public void onInitializeClient() {
        WizcraftParticleFactories.init();
        WizcraftRenderers.init();
        WizcraftGui.init();
        WizcraftKeybindings.init();
        WizcraftNetworkingClient.registerReceivers();
        ClientPlayerInventoryEvents.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            hud = new WizcraftHud(client);
            toolManager = new ToolManager();
        });
    }
}
