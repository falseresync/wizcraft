package ru.falseresync.wizcraft.common.block.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import ru.falseresync.wizcraft.api.WizcraftApi;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;
import ru.falseresync.wizcraft.common.names.WizNbtNames;
import ru.falseresync.wizcraft.lib.storage.SimpleSingleVariantStorage;

public class MagicCauldronBlockEntity extends BlockEntity implements SidedStorageBlockEntity {
    public final SimpleSingleVariantStorage<FluidVariant> fluidStorage = SimpleSingleVariantStorage.Builder.fluid()
            .supportsExtraction(false)
            .build(fluidVariant -> FluidConstants.BUCKET, this::markDirty);
    public final SimpleSingleVariantStorage<ItemVariant> itemStorage = SimpleSingleVariantStorage.Builder.item()
            .supportsExtraction(false)
            .build(itemVariant -> (long) itemVariant.getItem().getMaxCount(), this::markDirty);

    public MagicCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.MAGIC_CAULDRON, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MagicCauldronBlockEntity entity) {
        if (world.isClient) {
            return;
        }

        var fluidVariant = entity.fluidStorage.getResource();
        if (fluidVariant.isBlank()) {
            return;
        }

        var itemVariant = entity.itemStorage.getResource();
        if (itemVariant.isBlank()) {
            return;
        }

        var composition = WizcraftApi.getInstance().compositionsManager().forItem(itemVariant.getItem());
        if (composition.isPresent()) {
            var itemAmount = entity.itemStorage.getAmount();
            try (var tx = Transaction.openOuter()) {
                var removableAmount = entity.itemStorage.extract(itemVariant, itemAmount, tx);
                if (removableAmount == itemAmount) {
                    var elementAmounts = composition.get().elements().stream()
                            .map(elementAmount -> new ElementAmount(elementAmount.element(), elementAmount.amount() * itemAmount))
                            .toList();

                    tx.commit();
                }
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        var customNbt = new NbtCompound();
        fluidStorage.writeNbt(customNbt);
        itemStorage.writeNbt(customNbt);
        nbt.put(WizNbtNames.CUSTOM_NBT, customNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        var customNbt = nbt.getCompound(WizNbtNames.CUSTOM_NBT);
        fluidStorage.readNbt(customNbt);
        itemStorage.readNbt(customNbt);
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return fluidStorage;
    }

    @Override
    public Storage<ItemVariant> getItemStorage(Direction side) {
        return itemStorage;
    }
}