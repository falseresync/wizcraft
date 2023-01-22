package ru.falseresync.wizcraft.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;

public class BasicSingleItemStorage extends SingleItemStorage {
    protected final ResourceAmount<ItemVariant> data;
    protected final Runnable onChange;

    public BasicSingleItemStorage(Runnable onChange) {
        data = new ResourceAmount<>(ItemVariant.blank(), 0);
        this.onChange = onChange;
    }

    @Override
    protected long getCapacity(ItemVariant variant) {
        return variant.isBlank() ? 0 : variant.getItem().getMaxCount();
    }

    @Override
    protected void onFinalCommit() {
        onChange.run();
    }
}
