package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.client.hud.WizcraftHud;
import dev.falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import dev.falseresync.wizcraft.client.render.WizcraftRenderers;
import dev.falseresync.wizcraft.network.WizClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.mixin.event.lifecycle.client.ClientWorldMixin;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class WizcraftClient implements ClientModInitializer {
    public static WizcraftHud hud;

    @Override
    public void onInitializeClient() {
        WizcraftKeybindings.register();
        WizcraftRenderers.register();
        WizcraftParticleFactories.register();
        WizClientNetworking.registerReceivers();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            hud = new WizcraftHud(client);
        });
    }
}
