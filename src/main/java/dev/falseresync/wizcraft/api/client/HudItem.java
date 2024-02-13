package dev.falseresync.wizcraft.api.client;

import dev.falseresync.wizcraft.api.client.BetterDrawContext;

public interface HudItem {
    void render(BetterDrawContext context, float tickDelta);

    void tick();
}
