package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.api.common.HasId;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        return this.focus.asStack();
    }

    @Override
    public Focus getDefaultFocus() {
        return this.focus;
    }

    @Override
    public Identifier getId() {
        return this.id;
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
        return Text.translatable(getTranslationKey(), this.focus.getName().getString());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        Focus.fromStack(stack, this.focus).appendTooltip(stack, world, tooltip, context);
    }
}
