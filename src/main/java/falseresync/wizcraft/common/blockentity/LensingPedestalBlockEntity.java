package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class LensingPedestalBlockEntity extends BlockEntity {
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected @Nullable BlockPos linkedTo = null;

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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory.getHeldStacks(), registryLookup);

        if (linkedTo != null) {
            nbt.put(CommonKeys.LINKED_TO, NbtHelper.fromBlockPos(linkedTo));
        } else {
            nbt.remove(CommonKeys.LINKED_TO);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registryLookup);

        linkedTo = null;
        if (nbt.contains(CommonKeys.LINKED_TO, NbtElement.COMPOUND_TYPE)) {
            NbtHelper.toBlockPos(nbt, CommonKeys.LINKED_TO).ifPresent(it -> linkedTo = it);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}