package falseresync.wizcraft.client;

import falseresync.wizcraft.client.hud.WizcraftHud;
import falseresync.wizcraft.client.particle.WizcraftParticleFactories;
import falseresync.wizcraft.client.render.WizcraftRenderers;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.networking.WizcraftNetworkingClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class WizcraftClient implements ClientModInitializer {
    private static WizcraftHud hud;
    private static FocusManager focusManager;

    public static WizcraftHud getHud() {
        return hud;
    }

    public static FocusManager getFocusManager() {
        return focusManager;
    }

    @Override
    public void onInitializeClient() {
        WizcraftParticleFactories.init();
        WizcraftRenderers.init();
        WizcraftKeybindings.init();
        WizcraftNetworkingClient.registerReceivers();
        ClientPlayerInventoryEvents.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            hud = new WizcraftHud(client);
        });

        focusManager = new FocusManager();

        ClientPlayerInventoryEvents.SELECTED_SLOT_CHANGED.register((inventory, lastSelectedSlot) -> {
            var wandStack = inventory.getMainHandStack();
            if (wandStack.isIn(WizcraftItemTags.WANDS)) {
                hud.getWandChargeDisplay().upload(wandStack);
                hud.getWandChargeDisplay().show();
            } else {
                hud.getWandChargeDisplay().hide();
                hud.getFocusPicker().hide();
            }
        });
    }
}
