package ru.falseresync.wizcraft.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;

import java.util.function.Function;

public final class CodecUtil {
    private CodecUtil() {
    }

    public static <T> JsonDeserializer<T> toDeserializer(Codec<T> codec, Logger logger) {
        return (json, typeOfT, context) -> codec
                .parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, message -> logger.error("Unable to deserialize %s: %s".formatted(typeOfT.getTypeName(), message)));
    }

    /**
     * Null-safe Codec.PASSTHROUGH.comapFlatMap();
     *
     * @param encoder Potentially not-null-safe T to Json serializer
     * @param decoder Potentially not-null-safe Json to T deserializer
     * @return Null-safe codec
     * @author ResourcefulLib
     */
    public static <T> Codec<T> passthrough(Function<T, JsonElement> encoder, Function<JsonElement, T> decoder) {
        return Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
            if (dynamic.getValue() instanceof JsonElement jsonElement) {
                var output = clearNulls(jsonElement);
                if (output == null) {
                    return DataResult.error("Value was null for decoder: " + decoder);
                }
                return DataResult.success(decoder.apply(output));
            }
            return DataResult.error("Value was not an instance of JsonElement");
        }, input -> new Dynamic<>(JsonOps.INSTANCE, clearNulls(encoder.apply(input))));
    }

    /**
     * @return Null-safe Json or a null if the passed element itself is null
     * @author ResourcefulLib
     */
    private static JsonElement clearNulls(JsonElement json) {
        if (json instanceof JsonObject object) {
            var newObject = new JsonObject();
            for (var key : object.keySet()) {
                var element = clearNulls(object.get(key));
                if (element != null) {
                    newObject.add(key, element);
                }
            }
            return newObject;
        } else if (json instanceof JsonArray array) {
            var newArray = new JsonArray();
            for (var item : array) {
                var element = clearNulls(item);
                if (element != null) {
                    newArray.add(element);
                }
            }
            return newArray;
        }
        return json.isJsonNull() ? null : json;
    }
}
