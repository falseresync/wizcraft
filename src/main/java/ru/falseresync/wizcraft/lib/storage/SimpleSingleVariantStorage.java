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
    protected final VariantNbtDeserializer<T> nbtDeserializer;
    protected final Function<T, Long> capacityForVariant;
    protected final Runnable onChange;

    public SimpleSingleVariantStorage(T blankVariant, VariantNbtDeserializer<T> nbtDeserializer, Function<T, Long> capacityForVariant, Runnable onChange) {
        this.blankVariant = Objects.requireNonNull(blankVariant, "blankVariant may not be null");
        this.nbtDeserializer = Objects.requireNonNull(nbtDeserializer, "nbtDeserializer may not be null");
        this.capacityForVariant = Objects.requireNonNull(capacityForVariant, "capacityForVariant may not be null");
        this.onChange = Objects.requireNonNull(onChange, "onChange may not be null");

        // in the parent class this field gets initialized before this constructor
        // which means it's a null and leads to cryptic NPEs
        variant = blankVariant;
    }

    @Override
    protected T getBlankVariant() {
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

        public SimpleSingleVariantStorage<T> build(Function<T, Long> capacityForVariant, Runnable onChange) {
            return new SimpleSingleVariantStorage<>(blankVariant, nbtDeserializer, capacityForVariant, onChange);
        }
    }
}
