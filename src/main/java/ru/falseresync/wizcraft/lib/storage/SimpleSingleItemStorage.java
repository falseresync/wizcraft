package ru.falseresync.wizcraft.lib.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;

public abstract class SimpleSingleItemStorage extends SingleItemStorage {
    private final Runnable onChange;

    protected SimpleSingleItemStorage(Runnable onChange) {
        this.onChange = onChange;
    }

    @Override
    protected void onFinalCommit() {
        onChange.run();
    }

    public static SimpleSingleItemStorage withFixedCapacity(long capacity, Runnable onChange) {
        return new SimpleSingleItemStorage(onChange) {
            @Override
            protected long getCapacity(ItemVariant variant) {
                return 0;
            }
        };
    }

    public static SimpleSingleItemStorage withDefaultCapacity(Runnable onChange) {
        return new SimpleSingleItemStorage(onChange) {
            @Override
            protected long getCapacity(ItemVariant variant) {
                return variant.getItem().getMaxCount();
            }
        };
    }
}
