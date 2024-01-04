package dev.falseresync.wizcraft.client.gui.hud.slot;

import dev.falseresync.libhudcompat.LibHudCompat;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.HudController;
import dev.falseresync.wizcraft.api.client.gui.hud.slot.HudSlot;
import dev.falseresync.wizcraft.api.client.gui.hud.slot.WidgetTypePriority;
import dev.falseresync.wizcraft.common.Wizcraft;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import io.github.cottonmc.cotton.gui.widget.data.Vec2i;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class UnderBossBarHudSlot extends HudSlot {
    private @Nullable Vec2i cachedSize = null;
    private @Nullable Rect2i cachedRegion = null;
    private int verticalOffset = 0;
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "under_boss_bar");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public CottonHud.Positioner getPositioner() {
        return CottonHud.Positioner.horizontallyCentered(verticalOffset + 4 + 20 * findNumberOfBossBars());
    }

    @Override
    protected Rect2i getRegion(Vec2i widgetSize) {
        if (cachedRegion != null && cachedSize != null && cachedSize.equals(widgetSize)) {
            return cachedRegion;
        }
        cachedSize = widgetSize;
        var x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - widgetSize.x() / 2;
        var y = verticalOffset + 4 + 20 * findNumberOfBossBars();
        cachedRegion = new Rect2i(x, y, widgetSize.x(), widgetSize.y());
        return cachedRegion;
    }

    @Override
    public void occupy(HudController<?, ?> occupant, WidgetTypePriority priority, Vec2i widgetSize) {
        super.occupy(occupant, priority, widgetSize);
        var r = getRegion(widgetSize);
        LibHudCompat.addListener(r.x(), r.y(), r.width(), r.height(), this::offset);
    }

    @Override
    public void clear() {
        LibHudCompat.removeListener(this::offset);
        super.clear();
    }

    private static int findNumberOfBossBars() {
        return MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars.size();
    }

    private void offset(LibHudCompat.RegionChange change, int x, int y, int width, int height) {
        if (cachedRegion != null && cachedRegion.equals(new Rect2i(x, y, width, height))) return;
        if (change == LibHudCompat.RegionChange.OCCUPIED) {
            verticalOffset = y + height;
        } else {
            verticalOffset = 0;
        }
        getOccupant().ifPresent(HudController::reposition);
    }
}
