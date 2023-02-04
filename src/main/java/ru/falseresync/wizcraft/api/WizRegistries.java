package ru.falseresync.wizcraft.api;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.common.IdUtil;

public class WizRegistries {
    public static final Registry<Element> ELEMENT = FabricRegistryBuilder
            .createDefaulted(Element.class, IdUtil.id("element"), IdUtil.id("empty"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
}
