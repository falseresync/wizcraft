package falseresync.wizcraft.client;

import falseresync.wizcraft.client.hud.WizcraftHud;
import falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import falseresync.wizcraft.client.render.WizcraftRenderers;
import falseresync.wizcraft.networking.WizcraftNetworkingClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class WizcraftClient implements ClientModInitializer {
    public static WizcraftHud hud;

    @Override
    public void onInitializeClient() {
        WizcraftParticleFactories.init();
        WizcraftRenderers.init();
        WizcraftKeybindings.init();
        WizcraftNetworkingClient.registerReceivers();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            hud = new WizcraftHud(client);
        });
    }
}
