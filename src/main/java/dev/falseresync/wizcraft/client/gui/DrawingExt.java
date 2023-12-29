package dev.falseresync.wizcraft.client.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class DrawingExt {
    public static final int WHITE_TINT = Color.WHITE.toRgb();

    public static void square(DrawContext context, int x, int y, int size, Identifier texture, int tint, float opacity) {
        ScreenDrawing.texturedRect(context, x, y, size, size, texture, tint, opacity);
    }

    public static void square(DrawContext context, int x, int y, int size, Identifier texture, float opacity) {
        square(context, x, y, size, texture, WHITE_TINT, opacity);
    }

    public static void square(DrawContext context, int x, int y, int size, Identifier texture) {
        square(context, x, y, size, texture, WHITE_TINT, 1f);
    }
}
