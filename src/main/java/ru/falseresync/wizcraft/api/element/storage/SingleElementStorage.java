package ru.falseresync.wizcraft.api.element.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.common.init.WizElements;

public class SingleElementStorage extends SnapshotParticipant<ElementAmount> implements SingleSlotStorage<Element> {
    private final long capacity;
    private Element element = WizElements.EMPTY;
    private long amount = 0;

    public SingleElementStorage(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public long insert(Element insertedElement, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        elementNonBlank(insertedElement);

        if (insertedElement.equals(element) || isResourceBlank()) {
            long insertedAmount = Math.min(maxAmount, capacity - amount);

            if (insertedAmount > 0) {
                updateSnapshots(transaction);

                if (isResourceBlank()) {
                    element = insertedElement;
                    amount = insertedAmount;
                } else {
                    amount += insertedAmount;
                }

                return insertedAmount;
            }
        }

        return 0;
    }

    @Override
    public long extract(Element extractedElement, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        elementNonBlank(extractedElement);
        
        if (extractedElement.equals(element)) {
            long extractedAmount = Math.min(maxAmount, amount);

            if (extractedAmount > 0) {
                updateSnapshots(transaction);
                amount -= extractedAmount;

                if (amount == 0) {
                    element = WizElements.EMPTY;
                }

                return extractedAmount;
            }
        }

        return 0;
    }

    private static void elementNonBlank(Element element) {
        if (element == WizElements.EMPTY) {
            throw new IllegalArgumentException("Element may not be blank.");
        }
    }

    @Override
    public boolean isResourceBlank() {
        return element == WizElements.EMPTY;
    }

    @Override
    public Element getResource() {
        return element;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    protected ElementAmount createSnapshot() {
        return new ElementAmount(element, amount);
    }

    @Override
    protected void readSnapshot(ElementAmount snapshot) {
        element = snapshot.element();
        amount = snapshot.amount();
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.copyFrom((NbtCompound) ElementAmount.CODEC
                .encodeStart(NbtOps.INSTANCE, new ElementAmount(element, amount))
                .getOrThrow(false, errorMessage -> {}));
    }

    public void readNbt(NbtCompound nbt) {
        var elementAmount = ElementAmount.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow(false, errorMessage -> {});
        element = elementAmount.element();
        amount = elementAmount.amount();
    }
}
