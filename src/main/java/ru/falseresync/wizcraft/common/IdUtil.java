package ru.falseresync.wizcraft.common;

import net.minecraft.util.Identifier;

public final class IdUtil {
    private IdUtil() {
    }

    public static Identifier id(String id) {
        return new Identifier(Wizcraft.MODID, id);
    }
}
