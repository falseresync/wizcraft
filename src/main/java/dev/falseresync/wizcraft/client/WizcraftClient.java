package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.client.render.WizRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WizcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WizKeybindings.register();
        WizRenderers.register();
    }

}
