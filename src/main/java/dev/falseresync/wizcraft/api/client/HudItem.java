package dev.falseresync.wizcraft.api.client;

import net.minecraft.client.render.RenderTickCounter;

public interface HudItem {
    void render(BetterDrawContext context, RenderTickCounter tickCounter);

    void tick();
}
