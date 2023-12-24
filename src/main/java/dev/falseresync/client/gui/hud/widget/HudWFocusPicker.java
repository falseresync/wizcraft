package dev.falseresync.client.gui.hud.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class HudWFocusPicker extends WWidget implements RemovableHudWidget {
    protected int ticksToRemoval = 0;
    protected final ItemStack stack;

    public HudWFocusPicker(ItemStack stack) {
        this.stack = stack;
        ticksToRemoval = calculateTicksToRemoval();
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        RenderSystem.enableDepthTest();
        context.drawItemWithoutEntity(stack, x + getWidth() / 2 - 8, y + getHeight() / 2 - 8);
    }

    @Override
    public void tick() {
        if (ticksToRemoval > 0) {
            ticksToRemoval -= 1;
        }
    }

    @Override
    public boolean shouldBeRemoved() {
        return ticksToRemoval == 0;
    }

    @Override
    public void resetTicksToRemoval() {

    }

    public ItemStack getStack() {
        return stack;
    }
}
