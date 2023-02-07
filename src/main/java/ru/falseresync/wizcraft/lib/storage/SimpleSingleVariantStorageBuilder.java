package ru.falseresync.wizcraft.lib.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;

import java.util.function.Function;

public abstract sealed class SimpleSingleVariantStorageBuilder<T extends TransferVariant<?>>
        permits SimpleSingleVariantStorageBuilder.ItemBuilder, SimpleSingleVariantStorageBuilder.FluidBuilder {
    protected boolean supportsExtraction = true;
    protected boolean supportsInsertion = true;

    public static SimpleSingleVariantStorageBuilder<ItemVariant> item() {
        return new ItemBuilder();
    }

    public static SimpleSingleVariantStorageBuilder<FluidVariant> fluid() {
        return new FluidBuilder();
    }

    public SimpleSingleVariantStorageBuilder<T> supportsExtraction(boolean supportsExtraction) {
        this.supportsExtraction = supportsExtraction;
        return this;
    }

    public SimpleSingleVariantStorageBuilder<T> supportsInsertion(boolean supportsInsertion) {
        this.supportsInsertion = supportsInsertion;
        return this;
    }

    public SimpleSingleVariantStorageBuilder<T> readOnly() {
        this.supportsExtraction = false;
        this.supportsInsertion = false;
        return this;
    }

    public abstract SimpleSingleVariantStorage<T> build(Function<T, Long> capacityForVariant, Runnable onChange);

    protected static final class ItemBuilder extends SimpleSingleVariantStorageBuilder<ItemVariant> {
        @Override
        public SimpleSingleVariantStorage<ItemVariant> build(Function<ItemVariant, Long> capacityForVariant, Runnable onChange) {
            return new SimpleSingleVariantStorage<>(ItemVariant::blank, supportsExtraction, supportsInsertion, capacityForVariant, onChange) {
                @Override
                public void readNbt(NbtCompound nbt) {
                    variant = ItemVariant.fromNbt(nbt.getCompound("variant"));
                    amount = nbt.getLong("amount");
                }
            };
        }
    }

    protected static final class FluidBuilder extends SimpleSingleVariantStorageBuilder<FluidVariant> {
        @Override
        public SimpleSingleVariantStorage<FluidVariant> build(Function<FluidVariant, Long> capacityForVariant, Runnable onChange) {
            return new SimpleSingleVariantStorage<>(FluidVariant::blank, supportsExtraction, supportsInsertion, capacityForVariant, onChange) {
                @Override
                public void readNbt(NbtCompound nbt) {
                    variant = FluidVariant.fromNbt(nbt.getCompound("variant"));
                    amount = nbt.getLong("amount");
                }
            };
        }
    }
}
