package dev.falseresync.wizcraft.api;

import dev.falseresync.wizcraft.client.WizcraftClient;
import dev.falseresync.wizcraft.client.hud.WizcraftHud;

public final class WizcraftApi {
    public static WizcraftHud getHud() {
        return WizcraftClient.hud;
    }
}
