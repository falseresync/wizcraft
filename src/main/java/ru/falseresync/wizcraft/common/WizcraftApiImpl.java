package ru.falseresync.wizcraft.common;

import ru.falseresync.wizcraft.api.WizcraftApi;
import ru.falseresync.wizcraft.api.element.CompositionsManager;

public class WizcraftApiImpl implements WizcraftApi {
    private final CompositionsManager compositionsManager;

    public WizcraftApiImpl(CompositionsManager compositionsManager) {
        this.compositionsManager = compositionsManager;
    }

    @Override
    public CompositionsManager getCompositionsManager() {
        return compositionsManager;
    }
}
