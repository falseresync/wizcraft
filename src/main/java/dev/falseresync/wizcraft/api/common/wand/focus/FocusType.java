package dev.falseresync.wizcraft.api.common.wand.focus;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public record FocusType<T extends Focus>(Supplier<T> defaultFocusFactory, Codec<T> customDataCodec) {
    public ItemStack newStack() {
        return new ItemStack(defaultFocusFactory.get().getItem());
    }

    public FocusStack newFocusStack() {
        return new FocusStack(newStack());
    }
}
