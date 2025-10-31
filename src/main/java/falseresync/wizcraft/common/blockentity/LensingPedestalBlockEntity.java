package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.CommonKeys;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

import java.util.Optional;

public class LensingPedestalBlockEntity extends BlockEntity {
    protected final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected @Nullable BlockPos linkedTo = null;

    public LensingPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.LENSING_PEDESTAL, pos, state);
        inventory.addListener(sender -> setChanged());
    }

    // PUBLIC INTERFACE

    public void onCrafted(ItemStack remainder) {
        inventory.setItem(0, remainder);
    }

    public void linkTo(@Nullable BlockEntity controller) {
        linkedTo = controller != null ? controller.getBlockPos() : null;
        setChanged();
    }

    public boolean isLinked() {
        return linkedTo != null;
    }

    public boolean isLinkedTo(BlockEntity controller) {
        return !isLinked() || controller.getBlockPos().equals(linkedTo);
    }

    public ItemStack getHeldStackCopy() {
        return inventory.getItem(0).copy();
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public InventoryStorage getStorage() {
        return storage;
    }

    // DATA-SAVING INTERNALS

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            Optional.ofNullable(linkedTo).map(level::getBlockEntity).ifPresent(BlockEntity::setChanged);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);

        ContainerHelper.saveAllItems(nbt, inventory.getItems(), registryLookup);

        if (linkedTo != null) {
            nbt.put(CommonKeys.LINKED_TO, NbtUtils.writeBlockPos(linkedTo));
        } else {
            nbt.remove(CommonKeys.LINKED_TO);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        inventory.getItems().clear();
        ContainerHelper.loadAllItems(nbt, inventory.getItems(), registryLookup);

        linkedTo = null;
        if (nbt.contains(CommonKeys.LINKED_TO, Tag.TAG_COMPOUND)) {
            NbtUtils.readBlockPos(nbt, CommonKeys.LINKED_TO).ifPresent(it -> linkedTo = it);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }
}