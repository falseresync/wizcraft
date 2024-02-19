package dev.falseresync.wizcraft.client.hud;

import dev.falseresync.wizcraft.api.client.BetterDrawContext;
import dev.falseresync.wizcraft.client.hud.focuspicker.FocusPicker;
import dev.falseresync.wizcraft.client.hud.message.MessageDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;


@Environment(EnvType.CLIENT)
public class WizcraftHud {
    private final MinecraftClient client;
    private final MessageDisplay messageDisplay;
    private final FocusPicker focusPicker;

    public WizcraftHud(MinecraftClient client) {
        this.client = client;
        messageDisplay = new MessageDisplay(client, client.textRenderer);
        focusPicker = new FocusPicker(client, client.textRenderer);
        initEventListeners();
    }

    private void initEventListeners() {
        HudRenderCallback.EVENT.register((vanillaContext, tickDelta) -> {
            var context = new BetterDrawContext(client, vanillaContext);
            messageDisplay.render(context, tickDelta);
            focusPicker.render(context, tickDelta);
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (client.isPaused()) return;

            messageDisplay.tick();
            focusPicker.tick();
        });
    }

    public MessageDisplay getMessageDisplay() {
        return messageDisplay;
    }

    public FocusPicker getFocusPicker() {
        return focusPicker;
    }
}
