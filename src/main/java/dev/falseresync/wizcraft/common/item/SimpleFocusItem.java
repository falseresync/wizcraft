package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.common.skywand.focus.Focus;
import net.minecraft.item.Item;

public class SimpleFocusItem extends Item implements FocusItem {
    protected final Focus focus;

    public SimpleFocusItem(Settings settings, Focus focus) {
        super(settings);
        this.focus = focus;
    }

    @Override
    public Focus getFocus() {
        return focus;
    }
}
