package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizFocusTypes {
    public static final FocusType<ChargingFocus> CHARGING;
    public static final FocusType<StarshooterFocus> STARSHOOTER;
    public static final FocusType<LightningFocus> LIGHTNING;
    public static final FocusType<CometWarpFocus> COMET_WARP;
    private static final Map<Identifier, FocusType<?>> TO_REGISTER = new HashMap<>();

    static {
        CHARGING = r(new ChargingFocus());
        STARSHOOTER = r(new StarshooterFocus());
        LIGHTNING = r(new LightningFocus());
        COMET_WARP = r(new CometWarpFocus(), CometWarpFocus.CODEC);
    }

    private static <T extends Focus> FocusType<T> r(T focus) {
        return r(focus, Codec.unit(() -> focus));
    }

    private static <T extends Focus> FocusType<T> r(T defaultFocus, Codec<T> customDataCodec) {
        var type = new FocusType<>(defaultFocus, customDataCodec);
        TO_REGISTER.put(defaultFocus.getId(), type);
        return type;
    }

    public static void register(BiConsumer<Identifier, FocusType<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
