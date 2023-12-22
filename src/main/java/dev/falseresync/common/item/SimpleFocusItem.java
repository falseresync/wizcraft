package dev.falseresync.common.item;

import dev.falseresync.common.skywand.focus.Focus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

public class SimpleFocusItem extends Item implements FocusItem {
    protected final FocusFactory focusFactory;

    public SimpleFocusItem(Settings settings, FocusFactory focusFactory) {
        super(settings);
        this.focusFactory = focusFactory;
    }

    @Override
    public Focus getFocus(ItemStack stack) {
        var nbt = stack.getOrCreateNbt();

        if (nbt.contains("wizcraft:focus", NbtElement.COMPOUND_TYPE)) {
            var focusNbt = nbt.getCompound("wizcraft:focus");
            return focusFactory.fromNbt(focusNbt);
        }

        return focusFactory.fromNbt(null);
    }

    @FunctionalInterface
    public interface FocusFactory {
        Focus fromNbt(@Nullable NbtCompound nbt);
    }
}
