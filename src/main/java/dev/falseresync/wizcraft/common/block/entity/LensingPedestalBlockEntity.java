package dev.falseresync.wizcraft.common.block.entity;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LensingPedestalBlockEntity extends BlockEntity {
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);

    public LensingPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.LENSING_PEDESTAL, pos, state);
        inventory.addListener(sender -> markDirty());
    }

    public void onCrafted() {
        inventory.clear();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory.getHeldStacks());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks());
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

    public SimpleInventory getInventory() {
        return inventory;
    }

    public InventoryStorage getStorage() {
        return storage;
    }
}
