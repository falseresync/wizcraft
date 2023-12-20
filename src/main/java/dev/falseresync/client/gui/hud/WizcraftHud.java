package dev.falseresync.client.gui.hud;

import dev.falseresync.client.gui.hud.widget.HudWStatusLabel;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class WizcraftHud {
    protected static HudWStatusLabel statusLabel;

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (statusLabel != null) {
                statusLabel.tick();
                if (statusLabel.shouldBeRemoved()) {
                    CottonHud.remove(statusLabel);
                    statusLabel = null;
                }
            }
        });
    }

    public static void addOrReplaceStatusLabel(Text status) {
        if (statusLabel != null && statusLabel.getText().equals(status)) {
            statusLabel.resetTicksToRemoval();
        } else {
            if (statusLabel != null) {
                CottonHud.remove(statusLabel);
            }
            statusLabel = new HudWStatusLabel(status);
            CottonHud.add(statusLabel, CottonHud.Positioner.horizontallyCentered(20));
        }
    }
}
