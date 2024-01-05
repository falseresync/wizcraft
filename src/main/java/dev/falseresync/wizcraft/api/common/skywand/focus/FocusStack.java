package dev.falseresync.wizcraft.api.common.skywand.focus;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.common.CommonKeys;
import dev.falseresync.wizcraft.common.item.FocusItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;

public class FocusStack {
    private final ItemStack stack;
    private final Focus focus;

    public static final Codec<FocusStack> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ItemStack.CODEC.fieldOf(CommonKeys.STACK).forGetter(FocusStack::toItemStack),
                    Focus.CODEC.fieldOf(CommonKeys.FOCUS).forGetter(FocusStack::getFocus)
            ).apply(instance, FocusStack::new));

    private FocusStack(ItemStack stack, Focus focus) {
        this.stack = stack;
        this.focus = focus;
    }

    public FocusStack(ItemVariant variant) {
        this(variant.toStack());
    }

    public FocusStack(ItemStack stack) {
        Preconditions.checkArgument(stack.getItem() instanceof FocusItem, "Must be a stack of FocusItems");
        this.stack = stack.copy();
        this.focus = Focus.fromNbt(
                stack.getOrCreateSubNbt(CommonKeys.Namespaced.FOCUS),
                ((FocusItem) this.stack.getItem()).getDefaultFocus());
    }

    public ItemStack toItemStack() {
        stack.setSubNbt(CommonKeys.Namespaced.FOCUS, focus.toNbt());
        return stack;
    }

    public ItemVariant toItemVariant() {
        return ItemVariant.of(toItemStack());
    }

    public Focus getFocus() {
        return focus;
    }
}
