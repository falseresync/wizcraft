package falseresync.wizcraft.client.hud;

import falseresync.lib.client.BetterDrawContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class WizcraftHud {
    private final Minecraft client;
    private final FocusPickerHudItem focusPicker;
    private final ChargeDisplayHudItem chargeDisplay;

    public WizcraftHud(Minecraft client) {
        this.client = client;
        focusPicker = new FocusPickerHudItem(client, client.font);
        chargeDisplay = new ChargeDisplayHudItem(client, client.font);
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