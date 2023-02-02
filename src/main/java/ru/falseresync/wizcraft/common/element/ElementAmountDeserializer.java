package ru.falseresync.wizcraft.common.element;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.lib.names.WizJsonNames;

import java.lang.reflect.Type;

public class ElementAmountDeserializer implements JsonDeserializer<ElementAmount> {
    @Override
    public ElementAmount deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var elementAmountJson = JsonHelper.asObject(json, WizJsonNames.ELEMENT_AMOUNT);
        return new ElementAmount(
                WizRegistries.ELEMENT.get(new Identifier(JsonHelper.getString(elementAmountJson, WizJsonNames.ELEMENT))),
                JsonHelper.getLong(elementAmountJson, WizJsonNames.AMOUNT)
        );
    }
}
