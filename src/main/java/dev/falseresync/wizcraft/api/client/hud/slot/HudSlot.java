package dev.falseresync.wizcraft.api.client.hud.slot;

import dev.falseresync.libhudcompat.LibHudCompat;
import dev.falseresync.wizcraft.api.client.hud.controller.HudController;
import dev.falseresync.wizcraft.api.HasId;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import io.github.cottonmc.cotton.gui.widget.data.Vec2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class HudSlot implements HasId {
    private @Nullable HudController<?, ?> occupant = null;
    private WidgetTypePriority priority = WidgetTypePriority.NORMAL;

    public abstract CottonHud.Positioner getPositioner();

    protected abstract Rect2i getRegion(Vec2i widgetSize);

    public boolean canOccupy(Vec2i widgetSize, WidgetTypePriority priority) {
        return !isOccupied(widgetSize) || priority.getValue() >= this.priority.getValue();
    }

    public boolean isOccupied(Vec2i widgetSize) {
        if (occupant != null) {
            return true;
        }

        var r = getRegion(widgetSize);
        return !LibHudCompat.isRegionFree(r.x(), r.y(), r.width(), r.height());
    }

    public void occupy(HudController<?, ?> occupant, WidgetTypePriority priority, Vec2i widgetSize) {
        this.occupant = occupant;
        this.priority = priority;
        var r = getRegion(widgetSize);
        LibHudCompat.forceOccupyRegion(getId(), r.x(), r.y(), r.width(), r.height());
    }

    public void clear() {
        getOccupant().ifPresent(HudController::clear);
        occupant = null;
        priority = WidgetTypePriority.NORMAL;
        LibHudCompat.freeRegion(getId());
    }

    public void tick() {
        getOccupant().ifPresent(it -> {
            it.tick();
            if (it.getRemainingDisplayTicks() == 0) {
                clear();
            }
        });
    }

    public Optional<HudController<?, ?>> getOccupant() {
        return Optional.ofNullable(occupant);
    }
}
