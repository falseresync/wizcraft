package falseresync.wizcraft.client;

import falseresync.wizcraft.client.gui.*;
import falseresync.wizcraft.client.hud.*;
import falseresync.wizcraft.client.particle.*;
import falseresync.wizcraft.client.render.*;
import falseresync.wizcraft.common.config.*;
import falseresync.wizcraft.compat.lavender.*;
import falseresync.wizcraft.networking.*;
import me.shedaniel.autoconfig.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;

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
        AutoConfig.getGuiRegistry(WizcraftConfig.class).registerPredicateProvider(
                new TranslatableEnumGuiProvider<>(),
                field -> field.getType().isEnum() && field.isAnnotationPresent(TranslatableEnum.class)
        );

        WizcraftParticleFactories.init();
        WizcraftRendering.init();
        WizcraftGui.init();
        WizcraftKeybindings.init();
        WizcraftNetworkingClient.registerReceivers();
        ClientPlayerInventoryEvents.init();
        WizcraftLavenderPlugin.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            hud = new WizcraftHud(client);
            toolManager = new ToolManager();
        });
    }
}
