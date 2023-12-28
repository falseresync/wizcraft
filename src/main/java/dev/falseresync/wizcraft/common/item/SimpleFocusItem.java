package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.lib.HasId;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class SimpleFocusItem extends Item implements FocusItem, HasId {
    protected final Identifier id;
    protected final Focus focus;

    public SimpleFocusItem(Settings settings, Focus focus) {
        super(settings);
        this.focus = focus;
        this.id = focus.getId().withSuffixedPath("_focus");
    }

    @Override
    public ItemStack getDefaultStack() {
        return focus.asStack();
    }

    @Override
    public Focus getFocus() {
        return focus;
    }

    @Override
    public Identifier getId() {
        return id;
    }
}
