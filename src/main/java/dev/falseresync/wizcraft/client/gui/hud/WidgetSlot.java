package dev.falseresync.wizcraft.client.gui.hud;

import io.github.cottonmc.cotton.gui.client.CottonHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class WidgetSlot {
    private WidgetController<?, ?> occupant = null;
    private WidgetTypePriority priority = WidgetTypePriority.NORMAL;

    public abstract CottonHud.Positioner getPositioner();

    public boolean canOccupy(WidgetTypePriority priority) {
        return !isOccupied() || priority.getValue() >= this.priority.getValue();
    }

    public boolean isOccupied() {
        return this.occupant != null;
    }

    public void occupy(WidgetController<?, ?> occupant, WidgetTypePriority priority) {
        this.occupant = occupant;
        this.priority = priority;
    }

    public void clear() {
        this.occupant.clear();
        this.occupant = null;
        this.priority = WidgetTypePriority.NORMAL;
    }

    public void tick() {
        if (this.occupant != null) {
            this.occupant.tick();

            if (this.occupant.getRemainingDisplayTicks() == 0) {
                clear();
            }
        }
    }
}
