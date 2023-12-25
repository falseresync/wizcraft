package dev.falseresync.wizcraft.common.skywand.focus;

import dev.falseresync.wizcraft.lib.HasId;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizFocuses {
    public static final ChargingFocus CHARGING;
    public static final StarshooterFocus STARSHOOTER;
    private static final Map<Identifier, Focus> TO_REGISTER = new HashMap<>();

    static {
        CHARGING = r(new ChargingFocus());
        STARSHOOTER = r(new StarshooterFocus());
    }


    private static <T extends Focus & HasId> T r(T focus) {
        TO_REGISTER.put(focus.getId(), focus);
        return focus;
    }

    public static void register(BiConsumer<Identifier, Focus> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
