package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LensingPedestalBlockEntity extends BlockEntity {
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
//    protected @Nullable BlockPos linkedTo = null;

    public LensingPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.LENSING_PEDESTAL, pos, state);
        inventory.addListener(sender -> {
            setAttached(WizcraftDataAttachments.LENSING_PEDESTAL_INVENTORY, inventory.getHeldStacks());
        });
    }

    // PUBLIC INTERFACE

    public void onCrafted(ItemStack remainder) {
        inventory.setStack(0, remainder);
    }

    public void linkTo(@Nullable BlockEntity controller) {
        setAttached(WizcraftDataAttachments.LENSING_PEDESTAL_LINKED_TO, controller != null ? controller.getPos() : null);
//        markDirty();
    }

    public boolean isLinked() {
        return hasAttached(WizcraftDataAttachments.LENSING_PEDESTAL_LINKED_TO);
    }

    public boolean isLinkedTo(BlockEntity controller) {
        return !isLinked() || controller.getPos().equals(getAttached(WizcraftDataAttachments.LENSING_PEDESTAL_LINKED_TO));
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
            Optional.ofNullable(getAttached(WizcraftDataAttachments.LENSING_PEDESTAL_LINKED_TO))
                    .map(world::getBlockEntity)
                    .ifPresent(BlockEntity::markDirty);
        }
    }

//    @Override
//    protected void writeNbt(NbtCompound nbt) {
//        super.writeNbt(nbt);
//
//        Inventories.writeNbt(nbt, inventory.getHeldStacks());
//
//        if (linkedTo != null) {
//            nbt.put(CommonKeys.LINKED_TO, NbtHelper.fromBlockPos(linkedTo));
//        } else {
//            nbt.remove(CommonKeys.LINKED_TO);
//        }
//    }
//
//    @Override
//    public void readNbt(NbtCompound nbt) {
//        super.readNbt(nbt);
//
//        inventory.getHeldStacks().clear();
//        Inventories.readNbt(nbt, inventory.getHeldStacks());
//
//        linkedTo = null;
//        if (nbt.contains(CommonKeys.LINKED_TO, NbtElement.COMPOUND_TYPE)) {
//            linkedTo = NbtHelper.toBlockPos(nbt.getCompound(CommonKeys.LINKED_TO));
//        }
//    }
//
//    @Nullable
//    @Override
//    public Packet<ClientPlayPacketListener> toUpdatePacket() {
//        return BlockEntityUpdateS2CPacket.create(this);
//    }
//
//    @Override
//    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
//        return super.toInitialChunkDataNbt(registryLookup);
//    }
//
//    @Override
//    public NbtCompound toInitialChunkDataNbt() {
//        return createNbt();
//    }
}