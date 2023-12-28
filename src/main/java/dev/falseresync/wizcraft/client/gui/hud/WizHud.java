package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.client.gui.hud.widget.WFocusPicker;
import dev.falseresync.wizcraft.client.gui.hud.widget.WLabelWithSFX;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Deque;

@Environment(EnvType.CLIENT)
public class WizHud {
    protected static final WidgetSlot UNDER_BOSS_BAR = new WidgetSlot(CottonHud.Positioner.horizontallyCentered(20));
    public static final WidgetState<WFocusPicker, Deque<ItemStack>> FOCUS_PICKER;
    public static final WidgetState.ForStateful<WLabelWithSFX, Text> STATUS_MESSAGE;

    static {
        FOCUS_PICKER = new WidgetState<>(UNDER_BOSS_BAR, WFocusPicker::new);
        STATUS_MESSAGE = new WidgetState.ForStateful<>(UNDER_BOSS_BAR, text -> {
            var widget = new WLabelWithSFX(text);
            widget.enableFade();
            widget.enableShadow();
            widget.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return widget;
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            FOCUS_PICKER.tick();
            STATUS_MESSAGE.tick();
        });
    }
}
