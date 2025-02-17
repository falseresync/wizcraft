package falseresync.wizcraft.client.hud;

import falseresync.lib.client.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.*;

@Environment(EnvType.CLIENT)
public class WizcraftHud {
    private final MinecraftClient client;
    private final FocusPickerHudItem focusPicker;
    private final ChargeDisplayHudItem chargeDisplay;

    public WizcraftHud(MinecraftClient client) {
        this.client = client;
        focusPicker = new FocusPickerHudItem(client, client.textRenderer);
        chargeDisplay = new ChargeDisplayHudItem(client, client.textRenderer);
        initEventListeners();
    }

    private void initEventListeners() {
        HudRenderCallback.EVENT.register((vanillaContext, tickCounter) -> {
            var context = new BetterDrawContext(client, vanillaContext);
            focusPicker.render(context, tickCounter);
            chargeDisplay.render(context, tickCounter);
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (client.isPaused()) return;

            focusPicker.tick();
            chargeDisplay.tick();
        });
    }

    public FocusPickerHudItem getFocusPicker() {
        return focusPicker;
    }

    public ChargeDisplayHudItem getChargeDisplay() {
        return chargeDisplay;
    }
}