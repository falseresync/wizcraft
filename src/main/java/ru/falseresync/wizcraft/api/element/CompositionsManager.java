package ru.falseresync.wizcraft.api.element;

import net.minecraft.item.Item;

import java.util.Optional;

public interface CompositionsManager {
    Optional<Composition> forItem(Item item);
}
