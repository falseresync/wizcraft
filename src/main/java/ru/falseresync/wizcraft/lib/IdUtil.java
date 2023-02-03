package ru.falseresync.wizcraft.lib;

import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.common.Wizcraft;

public final class IdUtil {
    private IdUtil() {
    }

    public static Identifier wizId(String id) {
        return new Identifier(Wizcraft.MODID, id);
    }
}
