package dev.falseresync.wizcraft.api.common.skywand.focus;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public record FocusType<T extends Focus>(Supplier<T> defaultFocus, Codec<T> customDataCodec) {
    public ItemStack defaultStack() {
        return defaultFocus.get().getItem().getDefaultStack();
    }

    public FocusStack defaultFocusStack() {
        return new FocusStack(defaultStack());
    }
}
