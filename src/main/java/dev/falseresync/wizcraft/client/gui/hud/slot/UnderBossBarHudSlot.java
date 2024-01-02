package dev.falseresync.wizcraft.client.gui.hud.slot;

import dev.falseresync.wizcraft.api.client.gui.hud.slot.HudSlot;
import dev.falseresync.wizcraft.common.Wizcraft;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import io.github.cottonmc.cotton.gui.widget.data.Vec2i;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class UnderBossBarHudSlot extends HudSlot {
    private static final Identifier ID = new Identifier(Wizcraft.MODID, "under_boss_bar");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public CottonHud.Positioner getPositioner() {
        return CottonHud.Positioner.horizontallyCentered(4 + 20 * findNumberOfBossBars());
    }

    @Override
    protected Rect2i getRegion(Vec2i widgetSize) {
        var x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - widgetSize.x() / 2;
        var y = 4 + 20 * findNumberOfBossBars();
        return new Rect2i(x, y, widgetSize.x(), widgetSize.y());
    }

    private static int findNumberOfBossBars() {
        return MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars.size();
    }
}
