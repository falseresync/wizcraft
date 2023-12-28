package dev.falseresync.wizcraft.client.gui.oldhud;

import dev.falseresync.wizcraft.client.gui.oldhud.widget.WFocusPicker;
import dev.falseresync.wizcraft.client.gui.oldhud.widget.HudWStatusLabel;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

    public static final WFocusPicker FOCUS_PICKER = null;

    static {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            STATUS_LABEL.tick();
        });
    }
}
