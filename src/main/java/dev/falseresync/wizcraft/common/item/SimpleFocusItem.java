package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.api.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.api.common.skywand.focus.FocusStack;
import dev.falseresync.wizcraft.api.common.skywand.focus.FocusType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleFocusItem<T extends Focus> extends Item implements FocusItem, HasId {
    protected final Identifier id;
    protected final FocusType<T> focusType;

    public SimpleFocusItem(Settings settings, FocusType<T> focusType) {
        super(settings);
        this.focusType = focusType;
        this.id = focusType.defaultFocus().get().getId().withSuffixedPath("_focus");
    }

    @Override
    public T getDefaultFocus() {
        return focusType.defaultFocus().get();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public String getTranslationKey() {
        return "item.wizcraft.generic_simple_focus";
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }

    @Override
    public Text getName() {
        return Text.translatable(getTranslationKey(), focusType.defaultFocus().get().getName().getString());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        new FocusStack(stack).getFocus().appendTooltip(world, tooltip, context);
    }
}
