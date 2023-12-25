package dev.falseresync.wizcraft.client.gui.hud.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public interface RemovableHudWidget {
    boolean shouldBeRemoved();

    void resetTicksToRemoval();

    default int calculateTicksToRemoval() {
        return (int) (40 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
    }
}
