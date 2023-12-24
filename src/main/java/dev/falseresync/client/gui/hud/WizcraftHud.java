package dev.falseresync.client.gui.hud;

import dev.falseresync.client.gui.hud.widget.HudWFocusPicker;
import dev.falseresync.client.gui.hud.widget.HudWStatusLabel;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class WizcraftHud {
    public static final TrackedHudWidget<HudWStatusLabel, Text> STATUS_LABEL = new TrackedHudWidget<>() {
        @Override
        protected boolean compare(HudWStatusLabel widget, Text status) {
            return widget.getText().equals(status);
        }

        @Override
        protected HudWStatusLabel create(Text status) {
            return new HudWStatusLabel(status);
        }

        @Override
        protected CottonHud.Positioner getPositionerFor(HudWStatusLabel widget) {
            return CottonHud.Positioner.horizontallyCentered(20);
        }
    };

    public static final TrackedHudWidget<HudWFocusPicker, ItemStack> FOCUS_PICKER = new TrackedHudWidget<>() {
        @Override
        protected boolean compare(HudWFocusPicker widget, ItemStack stack) {
            return ItemStack.areEqual(widget.getStack(), stack);
        }

        @Override
        protected HudWFocusPicker create(ItemStack stack) {
            return new HudWFocusPicker(stack);
        }

        @Override
        protected CottonHud.Positioner getPositionerFor(HudWFocusPicker widget) {
            return CottonHud.Positioner.horizontallyCentered(20);
        }
    };

    static {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            STATUS_LABEL.tick();
            FOCUS_PICKER.tick();
        });
    }
}
