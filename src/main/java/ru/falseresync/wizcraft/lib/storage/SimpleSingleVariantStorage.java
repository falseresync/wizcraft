package ru.falseresync.wizcraft.lib.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SimpleSingleVariantStorage<T extends TransferVariant<?>> extends SingleVariantStorage<T> {
    protected final Supplier<T> blankVariant;
    protected final boolean supportsExtraction;
    protected final boolean supportsInsertion;
    protected final Function<T, Long> capacityForVariant;
    protected final Runnable onChange;

    public SimpleSingleVariantStorage(Supplier<T> blankVariant, boolean supportsExtraction, boolean supportsInsertion, Function<T, Long> capacityForVariant, Runnable onChange) {
        this.blankVariant = blankVariant;
        this.supportsExtraction = supportsExtraction;
        this.supportsInsertion = supportsInsertion;
        this.capacityForVariant = capacityForVariant;
        this.onChange = Objects.requireNonNull(onChange, "onChange may not be null");
    }

    @Override
    public boolean supportsExtraction() {
        return supportsExtraction;
    }

    @Override
    public boolean supportsInsertion() {
        return supportsInsertion;
    }

    @Override
    protected boolean canExtract(T variant) {
        return supportsExtraction;
    }

    @Override
    protected boolean canInsert(T variant) {
        return supportsInsertion;
    }

    @Override
    protected T getBlankVariant() {
        return blankVariant.get();
    }

    @Override
    protected long getCapacity(T variant) {
        return capacityForVariant.apply(variant);
    }

    @Override
    protected void onFinalCommit() {
        onChange.run();
    }

    public abstract void readNbt(NbtCompound nbt);
}
