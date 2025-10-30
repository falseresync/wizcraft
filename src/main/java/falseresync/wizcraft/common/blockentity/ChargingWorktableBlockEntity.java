package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.CommonKeys;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ChargingWorktableBlockEntity extends WorktableBlockEntity {
    protected final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected boolean charging = false;

    public ChargingWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.CHARGING_WORKTABLE, pos, state);
        inventory.addListener(sender -> setChanged());
    }

    public static void tick(Level world, BlockPos pos, BlockState state, ChargingWorktableBlockEntity worktable) {
        worktable.tick(world, pos, state);
    }

    @Override
    public SimpleContainer getInventory() {
        return inventory;
    }

    @Override
    public InventoryStorage getStorage() {
        return storage;
    }

    public ItemStack getHeldStackCopy() {
        return inventory.getItem(0).copy();
    }

    public boolean isCharging() {
        return charging;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public boolean shouldExchangeFor(ItemStack stack) {
        return stack.is(WizcraftItems.WAND) || stack.isEmpty();
    }

    @Override
    public void remove(Level world, BlockPos pos) {

    }

    protected void tick(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide()) {
            return;
        }

        var heldStack = inventory.getItem(0);
        if (!heldStack.is(WizcraftItems.WAND)) {
            if (charging) {
                charging = false;
                setChanged();
            }
            return;
        }

        if (Wizcraft.getChargeManager().isWandFullyCharged(heldStack)) return;

        if (world.isNight() && world.random.nextFloat() < 0.25 || world.random.nextFloat() < 0.0625) {
            heldStack.update(WizcraftComponents.WAND_CHARGE, 0, current -> current + 1);

            var isFullyCharged = Wizcraft.getChargeManager().isWandFullyCharged(heldStack);
            if (charging && isFullyCharged || !charging && !isFullyCharged) {
                charging = !charging;
                setChanged();
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            level.getBlockEntity(worldPosition.above(2), WizcraftBlockEntities.LENS).ifPresent(lens -> lens.setOn(charging));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);

        ContainerHelper.saveAllItems(nbt, inventory.getItems(), registryLookup);

        nbt.putBoolean(CommonKeys.CHARGING, charging);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        inventory.getItems().clear();
        ContainerHelper.loadAllItems(nbt, inventory.getItems(), registryLookup);

        charging = false;
        if (nbt.contains(CommonKeys.CHARGING, Tag.TAG_BYTE)) {
            charging = nbt.getBoolean(CommonKeys.CHARGING);
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