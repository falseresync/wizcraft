package ru.falseresync.wizcraft.common.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.falseresync.wizcraft.api.WizcraftApi;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.api.element.storage.SingleElementStorage;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;
import ru.falseresync.wizcraft.common.names.WizNbtNames;
import ru.falseresync.wizcraft.lib.storage.SimpleSingleVariantStorage;

public class MagicCauldronBlockEntity extends BlockEntity implements SidedStorageBlockEntity {
    protected final SimpleSingleVariantStorage<FluidVariant> fluidStorage = SimpleSingleVariantStorage.Builder.fluid()
            .build(fluidVariant -> FluidConstants.BUCKET, this::markDirty);
    protected final SimpleSingleVariantStorage<ItemVariant> itemStorage = SimpleSingleVariantStorage.Builder.item()
            .build(itemVariant -> (long) itemVariant.getItem().getMaxCount(), this::markDirty);

    public MagicCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.MAGIC_CAULDRON, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MagicCauldronBlockEntity entity) {
        if (world.isClient || entity.fluidStorage.isResourceBlank() || entity.itemStorage.isResourceBlank()) {
            return;
        }

        if (!entity.fluidStorage.getResource().isOf(Fluids.WATER)) {
            entity.processWithWater();
        }
    }

    protected void processWithWater() {
        var itemVariant = itemStorage.getResource();
        var composition = WizcraftApi.getInstance().compositionsManager().forItem(itemVariant.getItem());
        if (composition.isPresent()) {
            try (var tx = Transaction.openOuter()) {
                var itemAmount = itemStorage.getAmount();
                System.out.println(itemAmount);
                if (itemStorage.extract(itemVariant, itemAmount, tx) == itemAmount) {
                    var elementAmounts = composition.get().elements().stream()
                            .map(elementAmount -> new ElementAmount(elementAmount.element(), elementAmount.amount() * itemAmount))
                            .toList();

                    System.out.println(elementAmounts);
                    tx.commit();
                }
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        var fluidStorageNbt = new NbtCompound();
        fluidStorage.writeNbt(fluidStorageNbt);

        var itemStorageNbt = new NbtCompound();
        itemStorage.writeNbt(itemStorageNbt);

        var customNbt = new NbtCompound();
        customNbt.put(WizNbtNames.FLUID_STORAGE, fluidStorageNbt);
        customNbt.put(WizNbtNames.ITEM_STORAGE, itemStorageNbt);

        nbt.put(WizNbtNames.CUSTOM_NBT, customNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        var customNbt = nbt.getCompound(WizNbtNames.CUSTOM_NBT);

        var fluidStorageNbt = customNbt.getCompound(WizNbtNames.FLUID_STORAGE);
        fluidStorage.readNbt(fluidStorageNbt);

        var itemStorageNbt = customNbt.getCompound(WizNbtNames.ITEM_STORAGE);
        itemStorage.readNbt(itemStorageNbt);
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return FilteringStorage.insertOnlyOf(fluidStorage);
    }

    @Override
    @Nullable
    public Storage<ItemVariant> getItemStorage(Direction side) {
        // Item automation through pipes shouldn't be possible
        return FilteringStorage.readOnlyOf(itemStorage);
    }

    @Environment(EnvType.CLIENT)
    public FluidVariant getFluidVariant() {
        return fluidStorage.getResource();
    }

    public void interactWithItemEntity(ItemEntity itemEntity) {
        try (var tx = Transaction.openOuter()) {
            var stack = itemEntity.getStack();
            if (itemStorage.insert(ItemVariant.of(stack), stack.getCount(), tx) == stack.getCount()) {
                itemEntity.discard();
                tx.commit();
            }
        }
    }
}