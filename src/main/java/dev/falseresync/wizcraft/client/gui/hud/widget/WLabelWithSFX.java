package dev.falseresync.wizcraft.client.gui.hud.widget;

import dev.falseresync.wizcraft.client.gui.hud.WidgetController;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.TextAlignment;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class WLabelWithSFX extends WLabel implements WControllerAware {
    protected boolean hasShadow = false;
    protected boolean hasFade = false;
    protected WidgetController<?, ?> controller = null;

    public WLabelWithSFX(Text text, int color) {
        super(text, color);
    }

    public WLabelWithSFX(Text text) {
        this(text, Color.WHITE.toRgb());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        var fade = 0;
        if (this.hasFade) {
            if (this.controller == null || this.controller.getRemainingDisplayTicks() == 0) {
                return;
            }
            fade = Math.min(255, (int) (this.controller.getRemainingDisplayTicks() * 256F / 10F)) << 24;
        }

        int yOffset = TextAlignment.getTextOffsetY(this.verticalAlignment, this.height, 1);

        if (this.hasShadow) {
            ScreenDrawing.drawStringWithShadow(
                    context,
                    this.text.asOrderedText(),
                    this.horizontalAlignment,
                    x,
                    y + yOffset,
                    this.getWidth(),
                    (shouldRenderInDarkMode() ? this.darkmodeColor : this.color) + fade);
        } else {
            ScreenDrawing.drawString(
                    context,
                    this.text.asOrderedText(),
                    this.horizontalAlignment,
                    x,
                    y + yOffset,
                    this.getWidth(),
                    (shouldRenderInDarkMode() ? this.darkmodeColor : this.color) + fade);
        }

        Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
        ScreenDrawing.drawTextHover(context, hoveredTextStyle, x + mouseX, y + mouseY);
    }

    @Override
    public void setController(WidgetController<?, ?> controller) {
        if (this.hasFade) {
            this.controller = controller;
        }
    }

    public void enableShadow() {
        this.hasShadow = true;
    }

    public void enableFade() {
        this.hasFade = true;
    }
}
