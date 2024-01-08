package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetInstancePriority;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetQueryResponse;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusStack;
import dev.falseresync.wizcraft.common.wand.focus.WizFocuses;
import dev.falseresync.wizcraft.network.c2s.UpdateSkyWandFocusC2SPacket;
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
                var mainHandStack = client.player.getInventory().getMainHandStack();
                if (!mainHandStack.isOf(WizItems.SKY_WAND)) {
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
                    if (activeFocusStack.getFocus().getType() == WizFocuses.CHARGING) {
                        WizHud.Slots.UNDER_BOSS_BAR.clear();
                        WizHud.STATUS_MESSAGE.override(
                                Text.translatable("hud.wizcraft.sky_wand.no_focuses"),
                                WidgetInstancePriority.HIGH);
                        return;
                    } else if (inventory.getEmptySlot() == -1) {
                        WizHud.Slots.UNDER_BOSS_BAR.clear();
                        WizHud.STATUS_MESSAGE.override(
                                Text.translatable("hud.wizcraft.sky_wand.full_inventory"),
                                WidgetInstancePriority.HIGH);
                        return;
                    }
                }

                if (activeFocusStack.getFocus().getType() != WizFocuses.CHARGING) {
                    focuses.offerFirst(WizFocuses.CHARGING.defaultFocusStack());
                }
                focuses.offerFirst(activeFocusStack);

                var focusPickerQuery = WizHud.FOCUS_PICKER.getOrCreate(focuses);
                if (focusPickerQuery.status() == WidgetQueryResponse.Status.EXISTS && focusPickerQuery.widget() != null) {
                    WizHud.FOCUS_PICKER.resetDisplayTicks();
                    focusPickerQuery.widget().pickNext();
                    ClientPlayNetworking.send(new UpdateSkyWandFocusC2SPacket(focusPickerQuery.widget().getPicked().toItemVariant()));
                }
            }
        });
    }

    public static void register() {
    }
}
