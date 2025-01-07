package falseresync.wizcraft.client.hud;

import falseresync.lib.client.BetterDrawContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class WizcraftHud {
    private final MinecraftClient client;
    private final MessageDisplayHudItem messageDisplay;
    private final FocusPickerHudItem focusPicker;
    private final WandChargeDisplayHudItem wandChargeDisplay;

    public WizcraftHud(MinecraftClient client) {
        this.client = client;
        messageDisplay = new MessageDisplayHudItem(client, client.textRenderer);
        focusPicker = new FocusPickerHudItem(client, client.textRenderer);
        wandChargeDisplay = new WandChargeDisplayHudItem(client, client.textRenderer);
        initEventListeners();
    }

    private void initEventListeners() {
        HudRenderCallback.EVENT.register((vanillaContext, tickCounter) -> {
            var context = new BetterDrawContext(client, vanillaContext);
            messageDisplay.render(context, tickCounter);
            focusPicker.render(context, tickCounter);
            wandChargeDisplay.render(context, tickCounter);
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (client.isPaused()) return;

            messageDisplay.tick();
            focusPicker.tick();
            wandChargeDisplay.tick();
        });
    }

    public MessageDisplayHudItem getMessageDisplay() {
        return messageDisplay;
    }

    public FocusPickerHudItem getFocusPicker() {
        return focusPicker;
    }

    public WandChargeDisplayHudItem getWandChargeDisplay() {
        return wandChargeDisplay;
    }
}