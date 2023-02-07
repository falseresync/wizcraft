package ru.falseresync.wizcraft.common;

import com.google.gson.Gson;
import ru.falseresync.wizcraft.api.WizcraftApi;
import ru.falseresync.wizcraft.api.element.CompositionsManager;

public record WizcraftApiImpl(CompositionsManager compositionsManager) implements WizcraftApi {
    @Override
    public Gson getGson() {
        return Wizcraft.GSON;
    }
}
