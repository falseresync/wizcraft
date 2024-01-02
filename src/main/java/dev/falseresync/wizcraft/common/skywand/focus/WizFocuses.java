package dev.falseresync.wizcraft.common.skywand.focus;

import dev.falseresync.wizcraft.api.common.HasId;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizFocuses {
    public static final ChargingFocus CHARGING;
    public static final StarshooterFocus STARSHOOTER;
    public static final LightningFocus LIGHTNING;
    public static final CometWarpFocus COMET_WARP;
    private static final Map<Identifier, Focus> TO_REGISTER = new HashMap<>();

    static {
        CHARGING = r(new ChargingFocus());
        STARSHOOTER = r(new StarshooterFocus());
        LIGHTNING = r(new LightningFocus());
        COMET_WARP = r(new CometWarpFocus());
    }


    private static <T extends Focus & HasId> T r(T focus) {
        TO_REGISTER.put(focus.getId(), focus);
        return focus;
    }

    public static void register(BiConsumer<Identifier, Focus> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
