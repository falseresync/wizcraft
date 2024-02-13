package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.api.WizcraftApi;
import dev.falseresync.wizcraft.client.hud.WizcraftHud;
import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusStack;
import dev.falseresync.wizcraft.common.wand.focus.WizcraftFocuses;
import dev.falseresync.wizcraft.network.c2s.UpdateWandFocusC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public final class WizcraftKeybindings {
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
                var mainHandStack = client.player.getInventory().getMainHandStack();
                if (!mainHandStack.isOf(WizcraftItems.WAND)) {
                    return;
                }

                var inventory = client.player.getInventory();
                var focuses = inventory.main.stream()
                        .filter(stack -> stack.getItem() instanceof FocusItem)
                        .map(FocusStack::new)
                        .collect(Collectors.toCollection(ArrayDeque::new));

                var wand = Wand.fromStack(mainHandStack);
                var activeFocusStack = wand.getFocusStack();
                if (focuses.isEmpty()) {
                    if (activeFocusStack.getFocus().getType() == WizcraftFocuses.CHARGING) {
                        WizcraftApi.getHud().getMessageDisplay().postImportant(Text.translatable("hud.wizcraft.wand.no_focuses"));
                        return;
                    } else if (inventory.getEmptySlot() == -1) {
                        WizcraftApi.getHud().getMessageDisplay().postImportant(Text.translatable("hud.wizcraft.wand.full_inventory"));
                        return;
                    }
                }

                if (activeFocusStack.getFocus().getType() != WizcraftFocuses.CHARGING) {
                    focuses.offerFirst(WizcraftFocuses.CHARGING.newFocusStack());
                }
                focuses.offerFirst(activeFocusStack);

                ClientPlayNetworking.send(new UpdateWandFocusC2SPacket(WizcraftApi.getHud().getFocusPicker().update(focuses).toItemVariant()));
            }
        });
    }

    public static void register() {
    }
}
