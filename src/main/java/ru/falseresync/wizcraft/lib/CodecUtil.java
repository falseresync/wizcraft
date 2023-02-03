package ru.falseresync.wizcraft.lib;

import com.google.gson.JsonDeserializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;

public final class CodecUtil {
    private CodecUtil() {
    }

    public static <T> JsonDeserializer<T> codecToDeserializer(Codec<T> codec, Logger logger) {
        return (json, typeOfT, context) -> codec
                .parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, message -> logger.error("Unable to deserialize %s: %s".formatted(typeOfT.getTypeName(), message)));
    }
}
