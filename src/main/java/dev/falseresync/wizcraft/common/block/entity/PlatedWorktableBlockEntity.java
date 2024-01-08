package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import dev.falseresync.wizcraft.common.recipe.LensedWorktableRecipe;
import dev.falseresync.wizcraft.common.recipe.WizRecipes;
import dev.falseresync.wizcraft.common.report.WizReports;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlatedWorktableBlockEntity extends BlockEntity {
    public static final int PEDESTALS_SEARCH_COOLDOWN = 5;
    public static final int RECIPE_TIME = 60;
    protected final List<LensingPedestalBlockEntity> pedestals = new ArrayList<>();
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected final SimpleInventory combinedInventory = new SimpleInventory(5) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected int remainingPedestalsSearchCooldown = 0;
    protected int remainingRecipeTime = 0;
    protected @Nullable LensedWorktableRecipe currentRecipe;

    public PlatedWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.PLATED_WORKTABLE, pos, state);
        inventory.addListener(sender -> markDirty());
    }

    public static void tick(World world, BlockPos pos, BlockState state, PlatedWorktableBlockEntity worktable) {
        if (world.isClient()) return;

        if (worktable.currentRecipe == null) {
            if (worktable.remainingPedestalsSearchCooldown > 0) {
                worktable.remainingPedestalsSearchCooldown -= 1;
            } else {
                worktable.remainingPedestalsSearchCooldown = PEDESTALS_SEARCH_COOLDOWN;
                searchPedestals(world, pos, worktable);
                return;
            }
        } else {
            if (worktable.remainingRecipeTime > 0) {
                worktable.remainingRecipeTime -= 1;
                searchPedestals(world, pos, worktable);
                if (worktable.pedestals.size() < 4) {
                    worktable.interruptCrafting();
                    return;
                }

                worktable.updateCombinedInventory();
                if (!worktable.currentRecipe.matches(worktable.combinedInventory, world)) {
                    worktable.interruptCrafting();
                    return;
                }
            } else {
                worktable.finishCrafting();
                return;
            }
        }
    }

    protected static void searchPedestals(World world, BlockPos pos, PlatedWorktableBlockEntity worktable) {
        worktable.pedestals.clear();

        for (var pedestalPos : List.of(pos.north(2), pos.west(2), pos.south(2), pos.east(2))) {
            if (world.getBlockEntity(pedestalPos) instanceof LensingPedestalBlockEntity pedestal) {
                worktable.pedestals.add(pedestal);
            }
        }
    }

    public void interact(@Nullable PlayerEntity player) {
        if (world == null || world.isClient()) return;

        searchPedestals(world, pos, this);
        if (pedestals.size() < 4) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Report.trigger(serverPlayer, WizReports.Worktable.INVALID_PEDESTAL_FORMATION);
            }
            return;
        }

        updateCombinedInventory();
        world.getRecipeManager()
                .getFirstMatch(WizRecipes.LENSED_WORKTABLE, combinedInventory, world)
                .map(RecipeEntry::value)
                .ifPresent(this::beginCrafting);
    }

    protected void updateCombinedInventory() {
        combinedInventory.setStack(0, inventory.getStack(0).copy());
        for (int i = 0; i < pedestals.size(); i++) {
            combinedInventory.setStack(i + 1, pedestals.get(i).getHeldStackCopy());
        }
    }

    protected void beginCrafting(LensedWorktableRecipe recipe) {
        if (world == null || world.isClient()) return;

        currentRecipe = recipe;
        remainingRecipeTime = RECIPE_TIME;
        MultiplayerReport.trigger((ServerWorld) world, pos, null, WizReports.Worktable.CRAFTING);
    }

    protected void interruptCrafting() {
        if (world == null || world.isClient()) return;

        combinedInventory.clear();
        currentRecipe = null;
        remainingRecipeTime = RECIPE_TIME;
        MultiplayerReport.trigger((ServerWorld) world, pos, null, WizReports.Worktable.INTERRUPTED);
    }

    protected void finishCrafting() {
        if (world == null || world.isClient() || currentRecipe == null) return;

        inventory.setStack(0, currentRecipe.craft(null, world.getRegistryManager()));
        var remainders = currentRecipe.getRemainder(combinedInventory);
        for (int i = 0; i < pedestals.size(); i++) {
            pedestals.get(i).onCrafted(remainders.get(i + 1));
        }

        combinedInventory.clear();
        currentRecipe = null;
        remainingRecipeTime = 0;
        MultiplayerReport.trigger((ServerWorld) world, pos, null, WizReports.Worktable.SUCCESS);
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

    public InventoryStorage getStorage() {
        return storage;
    }
}
