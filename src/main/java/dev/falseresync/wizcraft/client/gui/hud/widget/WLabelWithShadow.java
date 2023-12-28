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

public class WLabelWithShadow extends WLabel {
    protected final boolean hasShadow;

    public WLabelWithShadow(Text text, int color, boolean hasShadow) {
        super(text, color);
        this.hasShadow = hasShadow;
    }

    public WLabelWithShadow(Text text, boolean hasShadow) {
        this(text, Color.WHITE.toRgb(), hasShadow);
    }

    public WLabelWithShadow(Text text) {
        this(text, true);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int yOffset = TextAlignment.getTextOffsetY(verticalAlignment, height, 1);

        if (hasShadow) {
            ScreenDrawing.drawStringWithShadow(context, text.asOrderedText(), horizontalAlignment, x, y + yOffset, this.getWidth(), shouldRenderInDarkMode() ? darkmodeColor : color);
        } else {
            ScreenDrawing.drawString(context, text.asOrderedText(), horizontalAlignment, x, y + yOffset, this.getWidth(), shouldRenderInDarkMode() ? darkmodeColor : color);
        }

        Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
        ScreenDrawing.drawTextHover(context, hoveredTextStyle, x + mouseX, y + mouseY);
    }
}
