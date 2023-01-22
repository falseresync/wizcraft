package ru.falseresync.wizcraft.block.entity;

import com.google.common.base.Predicates;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.falseresync.wizcraft.Wizcraft;
import ru.falseresync.wizcraft.data.WizNbtKeys;
import ru.falseresync.wizcraft.registry.WizBlockEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MagicCauldronBlockEntity extends BlockEntity implements SidedStorageBlockEntity, InsertionOnlyStorage<ItemVariant>, SidedInventory {
    public final SingleFluidStorage fluidStorage;
    protected final List<ResourceAmount<ItemVariant>> itemParts;
    protected final Participant participant;

    public MagicCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.MAGIC_CAULDRON, pos, state);
        fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::markDirty);
        itemParts = new ArrayList<>();
        participant = new Participant();
    }

    public static void tick(World world, BlockPos pos, BlockState state, MagicCauldronBlockEntity entity) {
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        var fluidStorageNbt = new NbtCompound();
        fluidStorage.writeNbt(fluidStorageNbt);
        nbt.put(WizNbtKeys.FLUID_STORAGE, fluidStorageNbt);

        var itemStorageNbt = new NbtList();
        for (var part : itemParts) {
            var partNbt = new NbtCompound();
            partNbt.put(WizNbtKeys.VARIANT, part.resource().toNbt());
            partNbt.putLong(WizNbtKeys.AMOUNT, part.amount());
            itemStorageNbt.add(partNbt);
        }
        nbt.put(WizNbtKeys.ITEM_STORAGE, itemStorageNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        fluidStorage.readNbt(nbt.getCompound(WizNbtKeys.FLUID_STORAGE));

        var itemStorageNbt = nbt.getList(WizNbtKeys.ITEM_STORAGE, NbtElement.COMPOUND_TYPE);
        for (var partNbt : itemStorageNbt)
            if (partNbt instanceof NbtCompound partNbtCompound) {
                itemParts.add(new ResourceAmount<>(ItemVariant.fromNbt(partNbtCompound.getCompound(WizNbtKeys.VARIANT)), partNbtCompound.getLong(WizNbtKeys.AMOUNT)));
            } else
                Wizcraft.LOGGER.debug("Tried to load an invalid item storage from NBT: {}", partNbt);
    }

    // SidedStorageBlockEntity
    @Override
    public SingleFluidStorage getFluidStorage(Direction side) {
        return fluidStorage;
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(Direction side) {
        return this;
    }

    // Storage
    @Override
    public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

        if (insertedVariant.isBlank())
            return 0;

        if (itemParts.size() > 0) {
            var index = itemParts.size() - 1;
            var part = itemParts.get(index);
            if (part.resource().equals(insertedVariant))
                itemParts.set(index, new ResourceAmount<>(insertedVariant, part.amount() + maxAmount));
            else
                itemParts.add(new ResourceAmount<>(insertedVariant, maxAmount));
        } else
            itemParts.add(new ResourceAmount<>(insertedVariant, maxAmount));

        participant.updateSnapshots(transaction);

        return maxAmount;
    }

    // Inventory
    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side != Direction.UP)
            return new int[0];
        // Always 1 slot more
        return IntStream.rangeClosed(0, size()).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // Always 1 slot more
        return dir == Direction.UP && slot == size();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int size() {
        return itemParts.size();
    }

    @Override
    public boolean isEmpty() {
        return itemParts.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= size())
            return ItemStack.EMPTY;

        var resourceAmount = itemParts.get(slot);
        return resourceAmount.resource().toStack((int) resourceAmount.amount());
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < size() - 1 || slot > size()) return;
        StorageUtil.move(InventoryStorage.of(new SimpleInventory(stack), null), this, Predicates.alwaysTrue(), stack.getCount(), null);
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        // There's no GUI
        return false;
    }

    @Override
    public void clear() {
        itemParts.clear();
    }

    protected class Participant extends SnapshotParticipant<List<ResourceAmount<ItemVariant>>> {
        @Override
        protected List<ResourceAmount<ItemVariant>> createSnapshot() {
            return List.copyOf(itemParts);
        }

        @Override
        protected void readSnapshot(List<ResourceAmount<ItemVariant>> snapshot) {
            itemParts.clear();
            itemParts.addAll(snapshot);
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    }
}
