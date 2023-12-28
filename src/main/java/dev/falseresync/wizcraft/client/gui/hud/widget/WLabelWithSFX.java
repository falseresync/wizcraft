package dev.falseresync.wizcraft.client.gui.hud.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.TextAlignment;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class WLabelWithSFX extends WLabel implements WStateful {
    protected boolean hasShadow = false;
    protected boolean hasFade = false;
    protected int ticksToRemoval = 0;

    public WLabelWithSFX(Text text, int color) {
        super(text, color);
    }

    public WLabelWithSFX(Text text) {
        this(text, Color.WHITE.toRgb());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int yOffset = TextAlignment.getTextOffsetY(verticalAlignment, height, 1);

        var fade = 0;
        if (hasFade) {
            fade = Math.min(255, (int) (ticksToRemoval * 256F / 10F)) << 24;
        }

        if (hasShadow) {
            ScreenDrawing.drawStringWithShadow(context, text.asOrderedText(), horizontalAlignment, x, y + yOffset, this.getWidth(), shouldRenderInDarkMode() ? darkmodeColor + fade : color + fade);
        } else {
            ScreenDrawing.drawString(context, text.asOrderedText(), horizontalAlignment, x, y + yOffset, this.getWidth(), shouldRenderInDarkMode() ? darkmodeColor + fade : color + fade);
        }

        Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
        ScreenDrawing.drawTextHover(context, hoveredTextStyle, x + mouseX, y + mouseY);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void statefulTick(int ticksToRemoval) {
        if (hasFade && ticksToRemoval > 0) {
            this.ticksToRemoval = ticksToRemoval;
        }
    }

    public void enableShadow() {
        hasShadow = true;
    }

    public void enableFade() {
        hasFade = true;
    }
}
