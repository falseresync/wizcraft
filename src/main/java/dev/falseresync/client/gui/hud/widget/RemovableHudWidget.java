package dev.falseresync.client.gui.hud.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RemovableHudWidget {
    boolean shouldBeRemoved();
    void resetTicksToRemoval();
}
