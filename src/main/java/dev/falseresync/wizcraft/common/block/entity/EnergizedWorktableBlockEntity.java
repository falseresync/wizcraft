package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.recipe.WizRecipes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnergizedWorktableBlockEntity extends BlockEntity {
    public static final int PEDESTAL_SEARCH_COOLDOWN = 5;
    protected int ticksBeforePedestalSearch = 0;
    protected final List<LensingPedestalBlockEntity> pedestals = new ArrayList<>();
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public void markDirty() {
            EnergizedWorktableBlockEntity.this.markDirty();
        }
    };
    public final InventoryStorage storage = InventoryStorage.of(this.inventory, null);

    public EnergizedWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.ENERGIZED_WORKTABLE, pos, state);
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

        if (this.pedestals.size() < 4) {
            return;
        }
//        this.pedestals.forEach(pedestal -> pedestal.inventory.getStack(0));

        var combinedInventory = new SimpleInventory(this.pedestals.size() + 1);
        combinedInventory.setStack(0, this.inventory.getStack(0));
        for (int i = 0; i < this.pedestals.size(); i++) {
            combinedInventory.setStack(i + 1, this.pedestals.get(i).inventory.getStack(0));
        }
//        System.out.println(combinedInventory);

        var result = getWorld().getRecipeManager()
                .getFirstMatch(WizRecipes.LENSED_WORKTABLE, combinedInventory, getWorld())
                .map(RecipeEntry::value)
                .map(recipe -> recipe.getResult(getWorld().getRegistryManager()))
                .orElse(ItemStack.EMPTY);
//        System.out.println(result);

        if (result.isEmpty()) {
            return;
        }

        this.inventory.clear();
        this.pedestals.forEach(pedestal -> {
            pedestal.inventory.clear();
            pedestal.markDirty();
        });
        this.inventory.setStack(0, result);
        markDirty();
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
