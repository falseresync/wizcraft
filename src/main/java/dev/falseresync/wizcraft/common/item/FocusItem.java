package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.common.skywand.focus.Focus;
import net.minecraft.item.ItemStack;


public interface FocusItem {
    default Focus getFocus(ItemStack stack) {
        return Focus.fromStack(stack, getDefaultFocus());
    }

    Focus getDefaultFocus();
}
