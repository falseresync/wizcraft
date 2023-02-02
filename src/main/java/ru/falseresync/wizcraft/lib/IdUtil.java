package ru.falseresync.wizcraft.lib;

import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.common.Wizcraft;

public class IdUtil {
    public static Identifier id(String id) {
        return new Identifier(Wizcraft.MODID, id);
    }
}
