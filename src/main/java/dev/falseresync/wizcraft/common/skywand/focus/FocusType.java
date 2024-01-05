package dev.falseresync.wizcraft.common.skywand.focus;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.api.common.HasId;
import dev.falseresync.wizcraft.common.item.FocusItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public record FocusType<T extends Focus>(
        T defaultFocus,

        Codec<T> customDataCodec) {
    public ItemStack defaultStack() {
        return defaultFocus.getItem().getDefaultStack();
    }

    public FocusStack defaultFocusStack() {
        return new FocusStack(defaultStack());
    }
}
