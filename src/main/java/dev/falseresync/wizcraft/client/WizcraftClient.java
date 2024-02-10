package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.client.hud.WizcraftHud;
import dev.falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import dev.falseresync.wizcraft.client.render.WizcraftRenderers;
import dev.falseresync.wizcraft.network.WizClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WizcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WizcraftKeybindings.register();
        WizcraftRenderers.register();
        WizcraftParticleFactories.register();
        WizClientNetworking.registerReceivers();

        WizcraftHud.init();
    }
}
