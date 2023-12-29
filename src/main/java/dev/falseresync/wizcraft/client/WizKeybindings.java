package dev.falseresync.wizcraft.client;

import dev.falseresync.wizcraft.client.gui.hud.WidgetInstancePriority;
import dev.falseresync.wizcraft.client.gui.hud.WidgetQueryResponse;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import dev.falseresync.wizcraft.network.UpdateSkyWandC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.commons.lang3.stream.Streams;
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

                var focuses = Streams.of(client.player.getInventory().main)
                        .filter(stack -> stack.getItem() instanceof FocusItem)
                        .collect(Collectors.toCollection(ArrayDeque::new));

                var wand = SkyWand.fromStack(mainHandStack);
                var activeFocus = wand.getActiveFocus();
                if (activeFocus.getType() == WizFocuses.CHARGING && focuses.isEmpty()) {
                    WizHud.STATUS_MESSAGE.override(
                            Text.translatable("hud.wizcraft.sky_wand.no_focuses"),
                            WidgetInstancePriority.HIGH);
                    return;
                }

                if (activeFocus.getType() != WizFocuses.CHARGING) {
                    focuses.offerFirst(WizFocuses.CHARGING.asStack());
                }
                focuses.offerFirst(activeFocus.asStack());

                var focusPicker = WizHud.FOCUS_PICKER.getOrCreate(focuses);
                if (focusPicker.status() == WidgetQueryResponse.Status.EXISTS && focusPicker.widget() != null) {
                    WizHud.FOCUS_PICKER.resetDisplayTicks();
                    focusPicker.widget().pickNext();
                    var pickedFocus = focusPicker.widget().getPicked();

                    ClientPlayNetworking.send(new UpdateSkyWandC2SPacket(ItemVariant.of(pickedFocus)));
                }
            }
        });
    }

    public static void register() {
    }
}
