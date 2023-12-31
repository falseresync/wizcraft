package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.common.recipe.WizRecipes;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnergizedWorktableBlockEntity extends BlockEntity {
    public static final int PEDESTAL_SEARCH_COOLDOWN = 5;
    protected final List<LensingPedestalBlockEntity> pedestals = new ArrayList<>();
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    public final InventoryStorage storage = InventoryStorage.of(this.inventory, null);
    protected int ticksBeforePedestalSearch = 0;

    public EnergizedWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.ENERGIZED_WORKTABLE, pos, state);
        this.inventory.addListener(sender -> markDirty());
    }

    public static void tick(World world, BlockPos pos, BlockState state, EnergizedWorktableBlockEntity worktable) {
        if (world.isClient()) {
            return;
        }

        if (worktable.ticksBeforePedestalSearch > 0) {
            worktable.ticksBeforePedestalSearch -= 1;
            return;
        }
        worktable.ticksBeforePedestalSearch = PEDESTAL_SEARCH_COOLDOWN;
        searchPedestals(world, pos, worktable);
    }

    protected static void searchPedestals(World world, BlockPos pos, EnergizedWorktableBlockEntity worktable) {
        worktable.pedestals.clear();

        var pedestalPositions = List.of(pos.north(2), pos.west(2), pos.south(2), pos.east(2));
        for (var pedestalPos : pedestalPositions) {
            if (world.getBlockEntity(pedestalPos) instanceof LensingPedestalBlockEntity pedestal) {
                worktable.pedestals.add(pedestal);
            }
        }
    }

    public void craft() {
        if (getWorld() == null || getWorld().isClient()) {
            return;
        }

        searchPedestals(getWorld(), getPos(), this);
        if (this.pedestals.size() < 4) {
            return;
        }

        var combinedInventory = new SimpleInventory(this.pedestals.size() + 1);
        combinedInventory.setStack(0, this.inventory.getStack(0));
        for (int i = 0; i < this.pedestals.size(); i++) {
            combinedInventory.setStack(i + 1, this.pedestals.get(i).inventory.getStack(0));
        }

        var result = getWorld().getRecipeManager()
                .getFirstMatch(WizRecipes.LENSED_WORKTABLE, combinedInventory, getWorld())
                .map(RecipeEntry::value)
                .map(recipe -> recipe.craft(combinedInventory, getWorld().getRegistryManager()))
                .orElse(ItemStack.EMPTY);

        if (result.isEmpty()) {
            return;
        }

        this.pedestals.forEach(LensingPedestalBlockEntity::onCrafted);
        this.inventory.setStack(0, result);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (getWorld() != null) {
            getWorld().emitGameEvent(GameEvent.BLOCK_ACTIVATE, getPos(), GameEvent.Emitter.of(getCachedState()));
            getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory.getHeldStacks());
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
