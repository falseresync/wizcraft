package dev.falseresync.wizcraft.api.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class BetterDrawContext extends DrawContext {
    public static final int NO_TINT = 0xFF_FFFFFF;

    public BetterDrawContext(MinecraftClient client, DrawContext context) {
        super(client, context.getVertexConsumers());
    }

    public void drawSquare(Identifier texture, int x, int y, int size) {
        drawSquare(texture, x, y, size, size);
    }

    public void drawSquare(Identifier texture, int x, int y, int size, int textureSize) {
        drawRect(texture, x, y, size, size, textureSize, textureSize);
    }

    public void drawRect(Identifier texture, int x, int y, int width, int height) {
        drawRect(texture, x, y, width, height, 16, 16);
    }

    public void drawRect(Identifier texture, int x, int y, int width, int height, int texWidth, int texHeight) {
        drawTexture(texture, x, y, 0, 0, width, height, texWidth, texHeight);
    }
}
