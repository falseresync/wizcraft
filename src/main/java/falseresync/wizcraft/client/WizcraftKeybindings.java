package falseresync.wizcraft.client;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.*;
import net.minecraft.client.option.*;
import net.minecraft.client.util.*;
import org.lwjgl.glfw.*;

@Environment(EnvType.CLIENT)
public final class WizcraftKeybindings {
    public static final KeyBinding TOOL_CONTROL = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wizcraft.tool_control",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            "keyCategory.wizcraft"
    ));

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            while (TOOL_CONTROL.wasPressed()) {
                WizcraftClient.getToolManager().onKeyPressed(client, client.player);
            }
        });
    }
}
