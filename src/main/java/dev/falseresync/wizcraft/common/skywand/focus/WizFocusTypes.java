package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class WizFocusTypes {
    public static final FocusType<ChargingFocus> CHARGING;
    public static final FocusType<StarshooterFocus> STARSHOOTER;
    public static final FocusType<LightningFocus> LIGHTNING;
    public static final FocusType<CometWarpFocus> COMET_WARP;
    private static final Map<Identifier, FocusType<?>> TO_REGISTER = new HashMap<>();

    static {
        CHARGING = r(ChargingFocus::new, ChargingFocus.CODEC);
        STARSHOOTER = r(StarshooterFocus::new);
        LIGHTNING = r(LightningFocus::new);
        COMET_WARP = r(CometWarpFocus::new, CometWarpFocus.CODEC);
    }

    private static <T extends Focus> FocusType<T> r(Supplier<T> defaultFocus) {
        return r(defaultFocus, Codec.unit(defaultFocus));
    }

    private static <T extends Focus> FocusType<T> r(Supplier<T> defaultFocus, Codec<T> customDataCodec) {
        var type = new FocusType<>(defaultFocus, customDataCodec);
        TO_REGISTER.put(defaultFocus.get().getId(), type);
        return type;
    }

    public static void register(BiConsumer<Identifier, FocusType<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
