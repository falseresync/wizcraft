package ru.falseresync.wizcraft.api;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerializable {
    NbtCompound toNbt();
}
