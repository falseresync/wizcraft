package ru.falseresync.wizcraft.api.element;

import com.google.gson.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import ru.falseresync.wizcraft.api.NbtSerializable;
import ru.falseresync.wizcraft.api.WizRegistries;

import java.lang.reflect.Type;

public class Element implements NbtSerializable {
    @Override
    public NbtCompound toNbt() {
        return null;
    }

    public static class Deserializer implements JsonDeserializer<Element> {
        @Override
        public Element deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return WizRegistries.ELEMENT.get(new Identifier(JsonHelper.asString(json, "element")));
        }
    }
}
