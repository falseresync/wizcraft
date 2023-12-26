package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.client.gui.hud.WizcraftHud;
import dev.falseresync.wizcraft.common.item.FocusItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.stream.Streams;
import org.lwjgl.glfw.GLFW;

public final class WizKeybindings {
    public static final KeyBinding TOOL_CONTROL;

    static {
        TOOL_CONTROL = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wizcraft.tool_control",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "keyCategory.wizcraft"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            while (TOOL_CONTROL.wasPressed()) {
                var storage = PlayerInventoryStorage.of(client.player);
                var focusStacks = Streams.of(storage.nonEmptyViews())
                        .filter(view -> view.getResource().getItem() instanceof FocusItem)
                        .map(view -> view.getResource().toStack())
                        .toList();

                if (focusStacks.isEmpty()) {
                    return;
                }

                if (TOOL_CONTROL.isPressed()) {
                    WizcraftHud.FOCUS_PICKER.setOrReplace(focusStacks.get(0));
                } else {
                    WizcraftHud.FOCUS_PICKER.clear();
                }
            }
        });
    }

    public static void register() {}
}
