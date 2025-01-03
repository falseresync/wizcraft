package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.client.hud.WizcraftHud;
import dev.falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import dev.falseresync.wizcraft.client.render.WizcraftRenderers;
import dev.falseresync.wizcraft.network.WizcraftClientNetworking;
import dev.falseresync.wizcraft.network.WizcraftNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@Environment(EnvType.CLIENT)
public class WizcraftClient implements ClientModInitializer {
    public static WizcraftHud hud;

    @Override
    public void onInitializeClient() {
        WizcraftKeybindings.register();
        WizcraftRenderers.register();
        WizcraftParticleFactories.register();
        WizcraftNetworking.registerPackets();
        WizcraftClientNetworking.registerReceivers();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            hud = new WizcraftHud(client);
        });
    }
}
