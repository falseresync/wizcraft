package falseresync.wizcraft.client;

import falseresync.wizcraft.client.gui.WizcraftGui;
import falseresync.wizcraft.client.hud.WizcraftHud;
import falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import falseresync.wizcraft.client.render.WizcraftRendering;
import falseresync.wizcraft.common.config.TranslatableEnum;
import falseresync.wizcraft.common.config.TranslatableEnumGuiProvider;
import falseresync.wizcraft.common.config.WizcraftConfig;
import falseresync.wizcraft.compat.lavender.WizcraftLavenderPlugin;
import falseresync.wizcraft.networking.WizcraftClientReceivers;
import me.shedaniel.autoconfig.AutoConfig;
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
        AutoConfig.getGuiRegistry(WizcraftConfig.class).registerPredicateProvider(
                new TranslatableEnumGuiProvider<>(),
                field -> field.getType().isEnum() && field.isAnnotationPresent(TranslatableEnum.class)
        );

        WizcraftParticleFactories.init();
        WizcraftRendering.init();
        WizcraftGui.init();
        WizcraftKeybindings.init();
        WizcraftClientReceivers.register();
        ClientPlayerInventoryEvents.init();
        WizcraftLavenderPlugin.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            hud = new WizcraftHud(client);
            toolManager = new ToolManager();
        });
    }
}
