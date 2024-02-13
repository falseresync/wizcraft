package dev.falseresync.wizcraft.client.hud.message;

import net.minecraft.text.Text;

public record Message(Text text, int maxLifetime, boolean important) {
    public static Message createDefault(Text text) {
        return new Message(text, 200, false);
    }

    public static Message createImportant(Text text) {
        return new Message(text, 200, true);
    }
}
