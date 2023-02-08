package ru.falseresync.wizcraft.common.block.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import ru.falseresync.wizcraft.api.WizcraftApi;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.api.storage.ElementVariant;
import ru.falseresync.wizcraft.api.storage.SingleElementStorage;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;
import ru.falseresync.wizcraft.common.names.WizNbtNames;
import ru.falseresync.wizcraft.lib.storage.SimpleSingleItemStorage;

public class MagicCauldronBlockEntity extends BlockEntity implements SidedStorageBlockEntity {
    protected final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::markDirty);
    protected final SimpleSingleItemStorage itemStorage = SimpleSingleItemStorage.withDefaultCapacity(this::markDirty);
    protected final SingleElementStorage elementStorage = SingleElementStorage.withDefaultCapacity(this::markDirty);

    public MagicCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.MAGIC_CAULDRON, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MagicCauldronBlockEntity entity) {
        if (world.isClient || entity.fluidStorage.isResourceBlank() || entity.itemStorage.isResourceBlank()) {
            return;
        }

        if (entity.fluidStorage.getResource().isOf(Fluids.WATER)) {
            entity.processWithWater();
        }
    }

    protected void processWithWater() {
        var itemVariant = itemStorage.getResource();
        var composition = WizcraftApi.getInstance().compositionsManager().forItem(itemVariant.getItem());
        if (composition.isPresent()) {
            try (var tx = Transaction.openOuter()) {
                var itemAmount = itemStorage.getAmount();
                if (itemStorage.extract(itemVariant, itemAmount, tx) == itemAmount) {
                    var elementAmounts = composition.get().elements().stream()
                            .map(elementAmount -> new ElementAmount(elementAmount.element(), elementAmount.amount() * itemAmount))
                            .toList();

                    var elementVariant = ElementVariant.of(elementAmounts.get(0).element());
                    var elementAmount = elementAmounts.get(0).amount();
                    try (var elementTx = tx.openNested()) {
                        if (elementStorage.insert(elementVariant, elementAmount, elementTx) == elementAmount) {
                            world.syncWorldEvent(WorldEvents.BREWING_STAND_BREWS, pos, 0);
                            elementTx.commit();
                        }
                    }

                    System.out.println(elementVariant + " " + elementAmount);
                    tx.commit();
                }
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        writeCustomNbt(nbt);
        writeCustomSyncableNbt(nbt);
    }

    protected void writeCustomNbt(NbtCompound nbt) {
        var customNbt = new NbtCompound();

        var itemStorageNbt = new NbtCompound();
        itemStorage.writeNbt(itemStorageNbt);
        customNbt.put(WizNbtNames.ITEM_STORAGE, itemStorageNbt);

        var elementStorageNbt = new NbtCompound();
        elementStorage.writeNbt(elementStorageNbt);
        customNbt.put(WizNbtNames.ELEMENT_STORAGE, elementStorageNbt);

        nbt.put(WizNbtNames.CUSTOM_NBT, customNbt);
    }

    protected void writeCustomSyncableNbt(NbtCompound nbt) {
        var customNbt = new NbtCompound();

        var fluidStorageNbt = new NbtCompound();
        fluidStorage.writeNbt(fluidStorageNbt);
        customNbt.put(WizNbtNames.FLUID_STORAGE, fluidStorageNbt);

        nbt.put(WizNbtNames.CUSTOM_SYNCABLE_NBT, customNbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        var nbt = new NbtCompound();
        writeCustomSyncableNbt(nbt);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains(WizNbtNames.CUSTOM_NBT)) {
            var customNbt = nbt.getCompound(WizNbtNames.CUSTOM_NBT);

            var itemStorageNbt = customNbt.getCompound(WizNbtNames.ITEM_STORAGE);
            itemStorage.readNbt(itemStorageNbt);

            var elementStorageNbt = customNbt.getCompound(WizNbtNames.ELEMENT_STORAGE);
            elementStorage.readNbt(elementStorageNbt);
        }

        if (nbt.contains(WizNbtNames.CUSTOM_SYNCABLE_NBT)) {
            var customNbt = nbt.getCompound(WizNbtNames.CUSTOM_SYNCABLE_NBT);
            var fluidStorageNbt = customNbt.getCompound(WizNbtNames.FLUID_STORAGE);
            fluidStorage.readNbt(fluidStorageNbt);
        }
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return FilteringStorage.insertOnlyOf(fluidStorage);
    }

    @Override
    public Storage<ItemVariant> getItemStorage(Direction side) {
        // Item automation through pipes shouldn't be possible
        return FilteringStorage.readOnlyOf(itemStorage);
    }

    public FluidVariant getFluidVariant() {
        return fluidStorage.getResource();
    }

    public boolean interactWithItemEntity(ItemEntity itemEntity) {
        var success = false;

        if (!fluidStorage.isResourceBlank()) {
            try (var tx = Transaction.openOuter()) {
                var stack = itemEntity.getStack();
                if (itemStorage.insert(ItemVariant.of(stack), stack.getCount(), tx) == stack.getCount()) {
                    success = true;
                    tx.commit();
                }
            }
        }

        return success;
    }

    public boolean interactWithFluidState(FluidState fluidState) {
        var success = false;

        if (fluidStorage.isResourceBlank()) {
            if (fluidState.isStill() && !fluidState.get(FlowableFluid.FALLING)) {
                try (var tx = Transaction.openOuter()) {
                    if (fluidStorage.insert(FluidVariant.of(fluidState.getFluid()), FluidConstants.BUCKET, tx) == FluidConstants.BUCKET) {
                        success = true;

                        tx.commit();
                    }
                }
            }
        }

        return success;
    }
}