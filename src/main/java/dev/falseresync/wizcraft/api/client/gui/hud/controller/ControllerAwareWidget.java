package dev.falseresync.wizcraft.api.client.gui.hud.controller;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ControllerAwareWidget {
    default void controllerTick(int remainingDisplayTicks) {}

    default void setController(HudController<?, ?> controller) {}
}
