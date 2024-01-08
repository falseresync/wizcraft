package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.api.annotation.MarksDirty;
import dev.falseresync.wizcraft.api.annotation.NotRequiresMarkDirty;
import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.CommonKeys;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlatedWorktableBlockEntity extends BlockEntity {
    public static final int IDLE_SEARCH_COOLDOWN = 5;
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
    protected int remainingIdleSearchCooldown = 0;
    protected int remainingCraftingTime = 0;
    protected int craftingTime = 0;
    protected @Nullable Identifier currentRecipeId;
    protected @Nullable LensedWorktableRecipe currentRecipe;

    public PlatedWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.PLATED_WORKTABLE, pos, state);
        inventory.addListener(sender -> markDirty());
    }

    // PUBLIC INTERFACE

    public static void tick(World world, BlockPos pos, BlockState state, PlatedWorktableBlockEntity worktable) {
        worktable.tick(world, pos, state);
    }

    protected void tick(World world, BlockPos pos, BlockState state) {
        if (currentRecipe == null && currentRecipeId != null) {
            world.getRecipeManager().get(currentRecipeId)
                    .flatMap(entry -> entry.value() instanceof LensedWorktableRecipe recipe
                            ? Optional.of(recipe)
                            : Optional.empty())
                    .ifPresentOrElse(this::initStaticRecipeData, this::reset);
        }

        if (currentRecipe != null) {
            tickCrafting(world, pos, currentRecipe);
            return;
        }

        tickIdle(world, pos);
    }

    protected void tickCrafting(World world, BlockPos pos, LensedWorktableRecipe recipe) {
        if (remainingCraftingTime <= 0) {
            finishCrafting();
            return;
        }

        remainingCraftingTime -= 1;
        searchPedestals(world, pos);
        if (pedestals.size() < 4) {
            interruptCrafting();
            return;
        }

        updateCombinedInventory();
        if (!recipe.matches(combinedInventory, world)) {
            interruptCrafting();
            return;
        }
    }

    protected void tickIdle(World world, BlockPos pos) {
        if (remainingIdleSearchCooldown > 0) {
            remainingIdleSearchCooldown -= 1;
            return;
        }

        remainingIdleSearchCooldown = IDLE_SEARCH_COOLDOWN;
        searchPedestals(world, pos);
    }

    public void interact(@Nullable PlayerEntity player) {
        if (world == null || world.isClient()) return;

        searchPedestals(world, pos);
        if (pedestals.size() < 4) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Report.trigger(serverPlayer, WizReports.Worktable.INVALID_PEDESTAL_FORMATION);
            }
            return;
        }

        updateCombinedInventory();
        world.getRecipeManager()
                .getFirstMatch(WizRecipes.LENSED_WORKTABLE, combinedInventory, world)
                .ifPresent(this::beginCrafting);
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    public InventoryStorage getStorage() {
        return storage;
    }

    // DATA-MUTATING INTERNALS

    @NotRequiresMarkDirty
    protected void searchPedestals(World world, BlockPos pos) {
        pedestals.clear();

        for (var pedestalPos : List.of(pos.north(2), pos.west(2), pos.south(2), pos.east(2))) {
            if (world.getBlockEntity(pedestalPos) instanceof LensingPedestalBlockEntity pedestal) {
                pedestals.add(pedestal);
            }
        }
    }

    @MarksDirty
    protected void updateCombinedInventory() {
        combinedInventory.setStack(0, inventory.getStack(0).copy());
        for (int i = 0; i < pedestals.size(); i++) {
            combinedInventory.setStack(i + 1, pedestals.get(i).getHeldStackCopy());
        }
    }

    @MarksDirty
    protected void beginCrafting(RecipeEntry<LensedWorktableRecipe> recipeEntry) {
        if (world == null || world.isClient()) return;

        currentRecipeId = recipeEntry.id();
        initStaticRecipeData(recipeEntry.value());
        remainingCraftingTime = recipeEntry.value().getCraftingTime();
        markDirty();

        MultiplayerReport.trigger((ServerWorld) world, pos, null, WizReports.Worktable.CRAFTING);
    }

    @MarksDirty
    protected void interruptCrafting() {
        if (world == null || world.isClient()) return;

        reset();
        MultiplayerReport.trigger((ServerWorld) world, pos, null, WizReports.Worktable.INTERRUPTED);
    }

    @MarksDirty
    protected void finishCrafting() {
        if (world == null || world.isClient() || currentRecipe == null) return;

        inventory.setStack(0, currentRecipe.craft(null, world.getRegistryManager()));
        var remainders = currentRecipe.getRemainder(combinedInventory);
        for (int i = 0; i < pedestals.size(); i++) {
            pedestals.get(i).onCrafted(remainders.get(i + 1));
        }

        reset();
        MultiplayerReport.trigger((ServerWorld) world, pos, null, WizReports.Worktable.SUCCESS);
    }

    @NotRequiresMarkDirty
    protected void initStaticRecipeData(LensedWorktableRecipe recipe) {
        currentRecipe = recipe;
        craftingTime = currentRecipe.getCraftingTime();
    }

    @MarksDirty
    protected void reset() {
        combinedInventory.clear();
        currentRecipeId = null;
        currentRecipe = null;
        craftingTime = 0;
        remainingCraftingTime = 0;
        remainingIdleSearchCooldown = 0;

        markDirty();
    }

    // DATA-SAVING INTERNALS

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

        if (currentRecipeId != null) {
            nbt.putString(CommonKeys.CURRENT_RECIPE, currentRecipeId.toString());
        } else {
            nbt.remove(CommonKeys.CURRENT_RECIPE);
        }

        if (remainingCraftingTime != 0) {
            nbt.putInt(CommonKeys.REMAINING_CRAFTING_TIME, remainingCraftingTime);
        } else {
            nbt.remove(CommonKeys.REMAINING_CRAFTING_TIME);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks());

        if (nbt.contains(CommonKeys.CURRENT_RECIPE, NbtElement.STRING_TYPE)) {
            currentRecipeId = Identifier.tryParse(nbt.getString(CommonKeys.CURRENT_RECIPE));
        }

        if (nbt.contains(CommonKeys.REMAINING_CRAFTING_TIME, NbtElement.INT_TYPE)) {
            remainingCraftingTime = nbt.getInt(CommonKeys.REMAINING_CRAFTING_TIME);
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
