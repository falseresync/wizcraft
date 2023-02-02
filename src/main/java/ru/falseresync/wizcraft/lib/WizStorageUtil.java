package ru.falseresync.wizcraft.lib;

import com.google.common.base.Predicates;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class WizStorageUtil {
    public static long insertStack(ItemStack stack, Storage<ItemVariant> storage) {
        return StorageUtil.move(InventoryStorage.of(new SimpleInventory(stack), null), storage, Predicates.alwaysTrue(), stack.getCount(), null);
    }
}
