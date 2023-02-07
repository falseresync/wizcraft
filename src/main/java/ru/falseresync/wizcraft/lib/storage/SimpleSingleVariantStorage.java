package ru.falseresync.wizcraft.lib.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;
import java.util.function.Function;

public class SimpleSingleVariantStorage<T extends TransferVariant<?>> extends SingleVariantStorage<T> {
    protected final T blankVariant;
    protected final boolean supportsExtraction;
    protected final boolean supportsInsertion;
    protected final VariantNbtDeserializer<T> nbtDeserializer;
    protected final Function<T, Long> capacityForVariant;
    protected final Runnable onChange;

    public SimpleSingleVariantStorage(T blankVariant, boolean supportsExtraction, boolean supportsInsertion, VariantNbtDeserializer<T> nbtDeserializer, Function<T, Long> capacityForVariant, Runnable onChange) {
        this.blankVariant = Objects.requireNonNull(blankVariant, "blankVariant may not be null");
        this.supportsExtraction = supportsExtraction;
        this.supportsInsertion = supportsInsertion;
        this.nbtDeserializer = Objects.requireNonNull(nbtDeserializer, "nbtDeserializer may not be null");
        this.capacityForVariant = Objects.requireNonNull(capacityForVariant, "capacityForVariant may not be null");
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
    protected final T getBlankVariant() {
        return blankVariant;
    }

    @Override
    protected long getCapacity(T variant) {
        return capacityForVariant.apply(variant);
    }

    @Override
    protected void onFinalCommit() {
        onChange.run();
    }

    public void readNbt(NbtCompound nbt) {
        variant = nbtDeserializer.fromNbt(nbt.getCompound("variant"));
        amount = nbt.getLong("amount");
    }

    @FunctionalInterface
    public interface VariantNbtDeserializer<T extends TransferVariant<?>> {
        T fromNbt(NbtCompound compound);
    }

    public static class Builder<T extends TransferVariant<?>> {
        protected final T blankVariant;
        protected final VariantNbtDeserializer<T> nbtDeserializer;
        protected boolean supportsExtraction = true;
        protected boolean supportsInsertion = true;

        public Builder(T blankVariant, VariantNbtDeserializer<T> nbtDeserializer) {
            this.blankVariant = blankVariant;
            this.nbtDeserializer = nbtDeserializer;
        }

        public static Builder<ItemVariant> item() {
            return new Builder<>(ItemVariant.blank(), ItemVariant::fromNbt);
        }

        public static Builder<FluidVariant> fluid() {
            return new Builder<>(FluidVariant.blank(), FluidVariant::fromNbt);
        }

        public Builder<T> supportsExtraction(boolean supportsExtraction) {
            this.supportsExtraction = supportsExtraction;
            return this;
        }

        public Builder<T> supportsInsertion(boolean supportsInsertion) {
            this.supportsInsertion = supportsInsertion;
            return this;
        }

        public Builder<T> readOnly() {
            this.supportsExtraction = false;
            this.supportsInsertion = false;
            return this;
        }

        public SimpleSingleVariantStorage<T> build(Function<T, Long> capacityForVariant, Runnable onChange) {
            return new SimpleSingleVariantStorage<>(blankVariant, supportsExtraction, supportsInsertion, nbtDeserializer, capacityForVariant, onChange);
        }
    }
}
