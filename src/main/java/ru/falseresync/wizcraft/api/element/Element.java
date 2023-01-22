package ru.falseresync.wizcraft.api.element;

import com.google.gson.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import ru.falseresync.wizcraft.api.NbtSerializable;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.data.WizJsonConstants;
import ru.falseresync.wizcraft.data.WizNbtConstants;

import java.lang.reflect.Type;

public class Element implements NbtSerializable {
    @Override
    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
        nbt.putString(WizNbtConstants.ELEMENT, WizRegistries.ELEMENT.getId(this).toString());
        return nbt;
    }

    public static Element fromNbt(NbtCompound nbt) {
        return WizRegistries.ELEMENT.get(new Identifier(nbt.getString(WizNbtConstants.ELEMENT)));
    }

    public static class Deserializer implements JsonDeserializer<Element> {
        @Override
        public Element deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return WizRegistries.ELEMENT.get(new Identifier(JsonHelper.asString(json, WizJsonConstants.ELEMENT)));
        }
    }
}
