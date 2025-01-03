package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.api.common.wand.focus.Focus;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusStack;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleFocusItem<T extends Focus> extends Item implements FocusItem, HasId {
    protected final Identifier id;
    protected final FocusType<T> focusType;
    protected final T defaultFocus;

    public SimpleFocusItem(Settings settings, FocusType<T> focusType) {
        super(settings);
        this.focusType = focusType;
        this.defaultFocus = focusType.defaultFocusFactory().get();
        this.id = defaultFocus.getId().withSuffixedPath("_focus");
    }

    @Override
    public T getDefaultFocus() {
        return defaultFocus;
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
        return Text.translatable(getTranslationKey(), defaultFocus.getName().getString());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        new FocusStack(stack).getFocus().appendTooltip(context, tooltip);
    }
}
