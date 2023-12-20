package dev.falseresync.client.gui.hud;

import dev.falseresync.client.gui.hud.widget.HudWStatusLabel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class WizcraftHud {
    public static final TrackableHudItem<HudWStatusLabel, Text> STATUS_LABEL = new TrackableHudItem<>() {
        @Override
        protected boolean compareByArgument(HudWStatusLabel widget, Text status) {
            return widget.getText().equals(status);
        }

        @Override
        protected HudWStatusLabel createWidget(Text status) {
            return new HudWStatusLabel(status);
        }
    };

    static {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            STATUS_LABEL.tick();
        });
    }
}
