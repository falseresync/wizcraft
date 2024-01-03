package dev.falseresync.wizcraft.client.gui.hud.slot;

import dev.falseresync.wizcraft.api.client.gui.hud.slot.HudSlot;
import dev.falseresync.wizcraft.common.Wizcraft;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import io.github.cottonmc.cotton.gui.widget.data.Vec2i;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class TopLeftHudSlot extends HudSlot {
    private static final Identifier ID = new Identifier(Wizcraft.MODID, "top_left");
    private static final CottonHud.Positioner DEFAULT_POSITIONER = CottonHud.Positioner.of(2, 2);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public CottonHud.Positioner getPositioner() {
        return DEFAULT_POSITIONER;
    }

    @Override
    protected Rect2i getRegion(Vec2i widgetSize) {
        return new Rect2i(2, 2, widgetSize.x(), widgetSize.y());
    }
}
