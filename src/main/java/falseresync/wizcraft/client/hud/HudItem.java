package falseresync.wizcraft.client.hud;

import falseresync.lib.client.BetterGuiGraphics;
import net.minecraft.client.DeltaTracker;

public interface HudItem {
    void render(BetterGuiGraphics context, DeltaTracker tickCounter);

    void tick();
}