package ru.falseresync.wizcraft.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import ru.falseresync.wizcraft.api.NbtSerializable;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.api.element.ElementStorage;

import java.util.Iterator;

public class SimpleElementStorage implements ElementStorage, NbtSerializable {
    @Override
    public long insert(Element resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(Element resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public Iterator<StorageView<Element>> iterator() {
        return null;
    }

    @Override
    public NbtCompound toNbt() {
        return null;
    }
}
