package dev.falseresync.client.gui.hud.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.TextAlignment;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class HudWStatusLabel extends WLabel implements RemovableHudWidget {
    protected boolean shouldBeRemoved = false;
    protected int ticksToRemoval = 0;

    public HudWStatusLabel(Text text, int color) {
        super(text, color);
        ticksToRemoval = calculateTicksToRemoval();
        darkmodeColor = color;
        horizontalAlignment = HorizontalAlignment.CENTER;
    }

    public HudWStatusLabel(Text text) {
        this(text, Color.WHITE.toRgb());
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (ticksToRemoval > 0) {
            int yOffset = TextAlignment.getTextOffsetY(verticalAlignment, height, 1);
            var fade = Math.min(255, (int) (ticksToRemoval * 256F / 10F));

            ScreenDrawing.drawStringWithShadow(
                    context,
                    text.asOrderedText(),
                    horizontalAlignment,
                    x,
                    y + yOffset,
                    this.getWidth(),
                    color + (fade << 24)
            );

            Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
            ScreenDrawing.drawTextHover(context, hoveredTextStyle, x + mouseX, y + mouseY);
        }
    }

    @Override
    @Deprecated
    public HudWStatusLabel disableDarkmode() {
        return this;
    }

    @Override
    @Deprecated
    public int getDarkmodeColor() {
        return color;
    }

    @Override
    @Deprecated
    public HudWStatusLabel setDarkmodeColor(int color) {
        return this;
    }

    @Override
    @Deprecated
    public HudWStatusLabel setColor(int color, int darkmodeColor) {
        this.color = color;
        return this;
    }

    protected int calculateTicksToRemoval() {
        return (int) (40 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
    }

    @Override
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }

    @Override
    public void resetTicksToRemoval() {
        ticksToRemoval = calculateTicksToRemoval();
    }

    @Override
    public void tick() {
        super.tick();
        ticksToRemoval -= 1;
        if (ticksToRemoval <= 0) {
            shouldBeRemoved = true;
        }
    }
}
