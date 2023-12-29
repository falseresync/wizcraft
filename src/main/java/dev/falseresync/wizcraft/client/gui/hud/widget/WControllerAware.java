package dev.falseresync.wizcraft.client.gui.hud.widget;

import dev.falseresync.wizcraft.client.gui.hud.WidgetController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface WControllerAware {
    default void controllerTick(int remainingDisplayTicks) {}

    default void setController(WidgetController<?, ?> controller) {}
}
