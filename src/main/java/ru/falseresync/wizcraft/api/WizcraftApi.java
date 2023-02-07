package ru.falseresync.wizcraft.api;

import com.google.gson.Gson;
import ru.falseresync.wizcraft.api.element.CompositionsManager;
import ru.falseresync.wizcraft.common.Wizcraft;

public interface WizcraftApi {
    static WizcraftApi getInstance() {
        var impl = Wizcraft.API;
        if (impl == null) {
            throw new IllegalStateException("Accessed Wizcraft API too early!");
        }
        return impl;
    }

    CompositionsManager compositionsManager();

    Gson getGson();
}
