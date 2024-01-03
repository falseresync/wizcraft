package dev.falseresync.wizcraft.client.gui.hud.widget;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.ControllerAwareWidget;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.HudController;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.TextAlignment;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class WLabelWithSFX extends WLabel implements ControllerAwareWidget {
    protected boolean hasShadow = false;
    protected boolean hasFade = false;
    protected HudController<?, ?> controller = null;

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
        if (hasFade) {
            if (controller == null || controller.getRemainingDisplayTicks() == 0) {
                return;
            }
            fade = Math.min(255, (int) (controller.getRemainingDisplayTicks() * 256F / 10F)) << 24;
        }

        int yOffset = TextAlignment.getTextOffsetY(verticalAlignment, height, 1);

        if (hasShadow) {
            ScreenDrawing.drawStringWithShadow(
                    context,
                    text.asOrderedText(),
                    horizontalAlignment,
                    x,
                    y + yOffset,
                    getWidth(),
                    (shouldRenderInDarkMode() ? darkmodeColor : color) + fade);
        } else {
            ScreenDrawing.drawString(
                    context,
                    text.asOrderedText(),
                    horizontalAlignment,
                    x,
                    y + yOffset,
                    getWidth(),
                    (shouldRenderInDarkMode() ? darkmodeColor : color) + fade);
        }

        Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
        ScreenDrawing.drawTextHover(context, hoveredTextStyle, x + mouseX, y + mouseY);
    }

    @Override
    public void setController(HudController<?, ?> controller) {
        if (hasFade) {
            this.controller = controller;
        }
    }

    public void enableShadow() {
        hasShadow = true;
    }

    public void enableFade() {
        hasFade = true;
    }
}
