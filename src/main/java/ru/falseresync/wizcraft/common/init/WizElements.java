package ru.falseresync.wizcraft.common.init;

import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.lib.IdUtil;

public class WizElements {
    public static final Element EMPTY;
    public static final Element EARTH;

    static {
        EMPTY = new Element();
        EARTH = new Element();
    }

    public static void register() {
        Registry.register(WizRegistries.ELEMENT, IdUtil.id("empty"), EMPTY);
        Registry.register(WizRegistries.ELEMENT, IdUtil.id("earth"), EARTH);
    }
}
