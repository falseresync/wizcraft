package ru.falseresync.wizcraft.common.block.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.common.element.ElementalComposition;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;
import ru.falseresync.wizcraft.common.init.WizRecipes;
import ru.falseresync.wizcraft.lib.names.WizNbtNames;

public class MagicCauldronBlockEntity extends BlockEntity implements SidedStorageBlockEntity {
    public final SingleFluidStorage fluidStorage;
    public final SimpleInventory inventory;
    public final Storage<ItemVariant> inventoryWrapper;

    public MagicCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.MAGIC_CAULDRON, pos, state);
        fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::markDirty);
        inventory = new SimpleInventory(2);
        inventoryWrapper = FilteringStorage.insertOnlyOf(InventoryStorage.of(inventory, null));
    }

    public static void tick(World world, BlockPos pos, BlockState state, MagicCauldronBlockEntity entity) {
        if (world.isClient || !entity.fluidStorage.getResource().isOf(Fluids.WATER)) {
            return;
        }

        var stack = entity.inventory.getStack(0);
        if (stack.isEmpty()) {
            return;
        }

        var recipes = world.getRecipeManager().listAllOfType(WizRecipes.CATALYTIC_CONDENSATION);
        if (recipes.size() == 0) {
            ElementalComposition.Manager.COMPOSITIONS.stream()
                    .filter(composition -> composition.ingredient().test(stack))
                    .findFirst()
                    .ifPresent(composition -> {
                        entity.inventory.removeStack(0);
                        entity.fluidStorage.extract(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, null);
                        for (ElementAmount compostionElementAmount : composition.elements()) {
                            var elementAmount = new ElementAmount(
                                    compostionElementAmount.element(),
                                    compostionElementAmount.amount() * stack.getCount()
                            );
                            // TODO: element storage
                        }
                    });
        } else {
            // TODO: recipe processing
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        var fluidStorageNbt = new NbtCompound();
        fluidStorage.writeNbt(fluidStorageNbt);
        nbt.put(WizNbtNames.FLUID_STORAGE, fluidStorageNbt);
        nbt.put(WizNbtNames.ITEM_STORAGE, inventory.toNbtList());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        fluidStorage.readNbt(nbt.getCompound(WizNbtNames.FLUID_STORAGE));
        inventory.readNbtList(nbt.getList(WizNbtNames.ITEM_STORAGE, NbtElement.COMPOUND_TYPE));
    }

    // SidedStorageBlockEntity
    @Override
    public SingleFluidStorage getFluidStorage(Direction side) {
        return fluidStorage;
    }

    @Override
    public Storage<ItemVariant> getItemStorage(Direction side) {
        return inventoryWrapper;
    }
}