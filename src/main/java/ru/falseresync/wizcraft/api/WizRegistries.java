package ru.falseresync.wizcraft.api;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.util.IdUtil;

public class WizRegistries {
    public static final Registry<Element> ELEMENT;

    static {
        ELEMENT = FabricRegistryBuilder.createDefaulted(Element.class, IdUtil.id("element"), IdUtil.id("empty")).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    }
}
