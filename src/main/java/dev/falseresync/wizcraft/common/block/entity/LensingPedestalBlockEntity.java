package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.api.annotation.dirty.Dirty;
import dev.falseresync.wizcraft.api.annotation.dirty.MarksDirty;
import dev.falseresync.wizcraft.common.CommonKeys;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LensingPedestalBlockEntity extends BlockEntity {
    protected final @MarksDirty SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final @MarksDirty InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected @Dirty
    @Nullable BlockPos linkedTo = null;

    public LensingPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.LENSING_PEDESTAL, pos, state);
        inventory.addListener(sender -> markDirty());
    }

    // PUBLIC INTERFACE

    public void onCrafted(ItemStack remainder) {
        inventory.setStack(0, remainder);
    }

    public void linkTo(@Nullable BlockEntity controller) {
        linkedTo = controller != null ? controller.getPos() : null;
        markDirty();
    }

    public boolean isLinked() {
        return linkedTo != null;
    }

    public boolean isLinkedTo(BlockEntity controller) {
        return !isLinked() || controller.getPos().equals(linkedTo);
    }

    public ItemStack getHeldStackCopy() {
        return inventory.getStack(0).copy();
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    public InventoryStorage getStorage() {
        return storage;
    }

    // DATA-SAVING INTERNALS

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            Optional.ofNullable(linkedTo).map(world::getBlockEntity).ifPresent(BlockEntity::markDirty);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        Inventories.writeNbt(nbt, inventory.getHeldStacks());

        if (linkedTo != null) {
            nbt.put(CommonKeys.LINKED_TO, NbtHelper.fromBlockPos(linkedTo));
        } else {
            nbt.remove(CommonKeys.LINKED_TO);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks());

        linkedTo = null;
        if (nbt.contains(CommonKeys.LINKED_TO, NbtElement.COMPOUND_TYPE)) {
            linkedTo = NbtHelper.toBlockPos(nbt.getCompound(CommonKeys.LINKED_TO));
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
