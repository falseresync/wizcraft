package ru.falseresync.wizcraft.api.element;

import ru.falseresync.wizcraft.api.WizRegistries;

public class Element {
    @Override
    public String toString() {
        return WizRegistries.ELEMENT.getId(this).toString();
    }
}
