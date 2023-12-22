package dev.falseresync.common.item;

import dev.falseresync.common.skywand.focus.Focus;
import net.minecraft.item.ItemStack;

public interface FocusItem {
    Focus getFocus(ItemStack stack);
}
