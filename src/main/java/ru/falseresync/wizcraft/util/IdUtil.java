package ru.falseresync.wizcraft.util;

import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.Wizcraft;

public class IdUtil {
    public static final Identifier id(String id) {
        return new Identifier(Wizcraft.MODID, id);
    }
}
