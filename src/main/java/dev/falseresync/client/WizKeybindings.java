package dev.falseresync.client;

import dev.falseresync.client.gui.hud.WizcraftHud;
import dev.falseresync.common.item.WizItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class WizKeybindings {
    public static final KeyBinding TOOL_CONTROL;

    static {
        TOOL_CONTROL = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wizcraft.tool_control", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_LEFT_CONTROL, // The keycode of the key
                "category.wizcraft" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            while (TOOL_CONTROL.isPressed()) {
//                WizcraftHud.FOCUS_PICKER.setOrReplace(WizItems.STARSHOOTER_FOCUS.getDefaultStack());
//            }
            while (TOOL_CONTROL.wasPressed()) {
                WizcraftHud.FOCUS_PICKER.setOrReplace(WizItems.STARSHOOTER_FOCUS.getDefaultStack());
//                WizcraftHud.FOCUS_PICKER.clear();
            }
        });
    }

    public static void register() {}
}
