package dev.falseresync.wizcraft.common.block.entity.worktable;

import dev.falseresync.wizcraft.api.annotation.dirty.Dirty;
import dev.falseresync.wizcraft.api.annotation.dirty.MarksDirty;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlockEntity;
import dev.falseresync.wizcraft.common.CommonKeys;
import dev.falseresync.wizcraft.common.block.entity.WizcraftBlockEntities;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChargingWorktableBlockEntity extends WorktableBlockEntity {
    protected final @MarksDirty SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final @MarksDirty InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected @Dirty boolean charging = false;

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
        if (world.isClient()) return;

        var heldStack = inventory.getStack(0);
        if (!heldStack.isOf(WizcraftItems.WAND)) {
            if (charging) {
                charging = false;
                markDirty();
            }
            return;
        }

        var wand = Wand.fromStack(heldStack);
        if (wand.isFullyCharged()) return;

        if (world.isNight() && world.random.nextFloat() < 0.25 || world.random.nextFloat() < 0.0625) {
            wand.addCharge(1);
            wand.attachTo(heldStack);

            if (charging && wand.isFullyCharged() || !charging && !wand.isFullyCharged()) {
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
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        Inventories.writeNbt(nbt, inventory.getHeldStacks());

        nbt.putBoolean(CommonKeys.CHARGING, charging);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks());

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
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
