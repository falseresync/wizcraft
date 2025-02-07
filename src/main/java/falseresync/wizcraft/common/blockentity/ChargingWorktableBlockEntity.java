package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.CommonKeys;
import falseresync.wizcraft.common.data.ChargeManager;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChargingWorktableBlockEntity extends WorktableBlockEntity {
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected boolean charging = false;

    public ChargingWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.CHARGING_WORKTABLE, pos, state);
        inventory.addListener(sender -> markDirty());
    }

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }

    @Override
    public InventoryStorage getStorage() {
        return storage;
    }

    public ItemStack getHeldStackCopy() {
        return inventory.getStack(0).copy();
    }

    public boolean isCharging() {
        return charging;
    }

    @Override
    public void activate(PlayerEntity player) {

    }

    @Override
    public boolean shouldExchangeFor(ItemStack stack) {
        return stack.isOf(WizcraftItems.WAND) || stack.isEmpty();
    }

    @Override
    public void remove(World world, BlockPos pos) {

    }

    public static void tick(World world, BlockPos pos, BlockState state, ChargingWorktableBlockEntity worktable) {
        worktable.tick(world, pos, state);
    }

    protected void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        var heldStack = inventory.getStack(0);
        if (!heldStack.isOf(WizcraftItems.WAND)) {
            if (charging) {
                charging = false;
                markDirty();
            }
            return;
        }

        if (ChargeManager.isWandFullyCharged(heldStack)) return;

        if (world.isNight() && world.random.nextFloat() < 0.25 || world.random.nextFloat() < 0.0625) {
            heldStack.apply(WizcraftDataComponents.WAND_CHARGE, 0, current -> current + 1);

            var isFullyCharged = ChargeManager.isWandFullyCharged(heldStack);
            if (charging && isFullyCharged || !charging && !isFullyCharged) {
                charging = !charging;
                markDirty();
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            world.getBlockEntity(pos.up(2), WizcraftBlockEntities.LENS).ifPresent(lens -> lens.setOn(charging));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory.getHeldStacks(), registryLookup);

        nbt.putBoolean(CommonKeys.CHARGING, charging);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registryLookup);

        charging = false;
        if (nbt.contains(CommonKeys.CHARGING, NbtElement.BYTE_TYPE)) {
            charging = nbt.getBoolean(CommonKeys.CHARGING);
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