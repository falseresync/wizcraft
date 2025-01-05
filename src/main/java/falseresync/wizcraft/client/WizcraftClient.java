package falseresync.wizcraft.client;

import falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import falseresync.wizcraft.client.render.WizcraftRenderers;
import falseresync.wizcraft.networking.WizcraftNetworkingClient;
import net.fabricmc.api.ClientModInitializer;

public class WizcraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WizcraftParticleFactories.register();
        WizcraftRenderers.init();
        WizcraftNetworkingClient.registerReceivers();
    }
}
