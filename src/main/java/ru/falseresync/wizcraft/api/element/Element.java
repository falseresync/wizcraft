package ru.falseresync.wizcraft.api.element;

import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.storage.ElementVariant;
import ru.falseresync.wizcraft.common.storage.ElementVariantImpl;

public class Element {
    private final ElementVariant cachedVariant = new ElementVariantImpl(this, null);

    public final ElementVariant getCachedVariant() {
        return cachedVariant;
    }

    @Override
    public final String toString() {
        return WizRegistries.ELEMENT.getId(this).toString();
    }
}
