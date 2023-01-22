package ru.falseresync.wizcraft.client.util;

import net.minecraft.util.math.ColorHelper;

public record Color(float a, float r, float g, float b) {
    public static Color fromArgb(int color) {
        return new Color(ColorHelper.Argb.getAlpha(color) / 256f, ColorHelper.Argb.getRed(color) / 256f, ColorHelper.Argb.getGreen(color) / 256f, ColorHelper.Argb.getBlue(color) / 256f);
    }
}
