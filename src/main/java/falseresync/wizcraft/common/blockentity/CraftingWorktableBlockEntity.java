package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.CommonKeys;
import falseresync.wizcraft.common.recipe.LensedWorktableRecipe;
import falseresync.wizcraft.common.recipe.SimpleInventoryRecipeInput;
import falseresync.wizcraft.common.recipe.WizcraftRecipeTypes;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CraftingWorktableBlockEntity extends WorktableBlockEntity {
    public static final int IDLE_SEARCH_COOLDOWN = 5;
    protected final List<LensingPedestalBlockEntity> pedestals = new ArrayList<>();
    protected final List<BlockPos> nonEmptyPedestalPositions = new ArrayList<>();
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected final SimpleInventoryRecipeInput virtualCombinedInventory = new SimpleInventoryRecipeInput(5) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected int remainingIdleSearchCooldown = 0;
    protected int remainingCraftingTime = 0;
    protected int craftingTime = 0;
    protected ItemStack currentlyCrafted = ItemStack.EMPTY;
    protected @Nullable Identifier currentRecipeId;
    protected @Nullable LensedWorktableRecipe currentRecipe;

    public CraftingWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.CRAFTING_WORKTABLE, pos, state);
        inventory.addListener(sender -> markDirty());
    }

    // PUBLIC INTERFACE

    @Override
    public void activate(@Nullable PlayerEntity player) {
        if (world == null || world.isClient()) return;

        searchPedestals(world, pos);
        if (pedestals.size() < 4) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                WizcraftReports.WORKTABLE_INCOMPLETE.sendTo(serverPlayer);
            }
            return;
        }

        updateVirtualCombinedInventory();
        world.getRecipeManager()
                .getFirstMatch(WizcraftRecipeTypes.LENSED_WORKTABLE, virtualCombinedInventory, world)
                .ifPresent(this::beginCrafting);
    }

    @Override
    public void remove(World world, BlockPos pos) {
        searchPedestals(world, pos);
        pedestals.forEach(pedestal -> pedestal.linkTo(null));
    }

    public List<BlockPos> getNonEmptyPedestalPositions() {
        return nonEmptyPedestalPositions;
    }

    public Progress getProgress() {
        return new Progress(currentlyCrafted,
                remainingCraftingTime,
                craftingTime - remainingCraftingTime,
                1 - (float) remainingCraftingTime / craftingTime);
    }

    public ItemStack getHeldStackCopy() {
        return inventory.getStack(0).copy();
    }

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }

    @Override
    public InventoryStorage getStorage() {
        return storage;
    }

    // TICKERS

    public static void tick(World world, BlockPos pos, BlockState state, CraftingWorktableBlockEntity worktable) {
        worktable.tick(world, pos, state);
    }

    protected void tick(World world, BlockPos pos, BlockState state) {
        if (currentRecipe == null && currentRecipeId != null) {
            updateVirtualCombinedInventory();
            world.getRecipeManager().get(currentRecipeId)
                    .flatMap(entry -> entry.value() instanceof LensedWorktableRecipe recipe
                            ? Optional.of(recipe)
                            : Optional.empty())
                    .ifPresentOrElse(recipe -> initStaticRecipeData(world, recipe), this::reset);
        }

        if (world.isClient) return;

        if (currentRecipe != null) {
            tickCrafting(world, pos, currentRecipe);
            return;
        }

        tickIdle(world, pos);
    }

    protected void tickCrafting(World world, BlockPos pos, LensedWorktableRecipe recipe) {
        remainingCraftingTime -= 1;
        searchPedestals(world, pos);
        markDirty();
        if (pedestals.size() < 4) {
            interruptCrafting();
            return;
        }

        updateVirtualCombinedInventory();
        if (!recipe.matches(virtualCombinedInventory, world)) {
            interruptCrafting();
            return;
        }

        if (remainingCraftingTime <= 0) {
            finishCrafting();
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

    // DATA-MUTATING INTERNALS

    protected void searchPedestals(World world, BlockPos pos) {
        pedestals.clear();

        for (var pedestalPos : List.of(pos.north(2), pos.west(2), pos.south(2), pos.east(2))) {
            if (world.getBlockEntity(pedestalPos) instanceof LensingPedestalBlockEntity pedestal) {
                if (!pedestal.isLinkedTo(this)) {
                    world.breakBlock(pos, true);
                    WizcraftReports.WORKTABLE_CANNOT_PLACE.sendAround((ServerWorld) world, pos, null);
                } else {
                    pedestals.add(pedestal);
                    pedestal.linkTo(this);
                }
            }
        }
    }

    protected void updateVirtualCombinedInventory() {
        virtualCombinedInventory.setStack(0, inventory.getStack(0).copy());
        for (int i = 0; i < pedestals.size(); i++) {
            virtualCombinedInventory.setStack(i + 1, pedestals.get(i).getHeldStackCopy());
        }
    }

    protected void beginCrafting(RecipeEntry<LensedWorktableRecipe> recipeEntry) {
        if (world == null || world.isClient()) return;

        currentRecipeId = recipeEntry.id();
        initStaticRecipeData(world, recipeEntry.value());
        remainingCraftingTime = recipeEntry.value().getCraftingTime();
        markDirty();

        WizcraftReports.WORKTABLE_CRAFTING.sendAround((ServerWorld) world, pos, null);
    }

    protected void interruptCrafting() {
        if (world == null || world.isClient()) return;

        reset();
        WizcraftReports.WORKTABLE_INTERRUPTED.sendAround((ServerWorld) world, pos, null);
    }

    protected void finishCrafting() {
        if (world == null || world.isClient() || currentRecipe == null) return;

        inventory.setStack(0, currentRecipe.craft(virtualCombinedInventory, world.getRegistryManager()));
        var remainders = currentRecipe.getRemainder(virtualCombinedInventory);
        for (int i = 0; i < pedestals.size(); i++) {
            pedestals.get(i).onCrafted(remainders.get(i + 1));
        }

        reset();
        WizcraftReports.WORKTABLE_SUCCESS.sendAround((ServerWorld) world, pos, null);
    }

    protected void initStaticRecipeData(World world, LensedWorktableRecipe recipe) {
        currentRecipe = recipe;
        craftingTime = recipe.getCraftingTime();
        currentlyCrafted = currentRecipe.craft(virtualCombinedInventory, world.getRegistryManager());
    }

    protected void reset() {
        virtualCombinedInventory.clear();
        currentRecipeId = null;
        currentRecipe = null;
        craftingTime = 0;
        currentlyCrafted = ItemStack.EMPTY;
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory.getHeldStacks(), registryLookup);

        if (!pedestals.isEmpty()) {
            var nbtList = pedestals.stream()
                    .filter(pedestal -> !pedestal.getHeldStackCopy().isEmpty())
                    .map(BlockEntity::getPos)
                    .map(NbtHelper::fromBlockPos)
                    .map(it -> {
                        var tag = new NbtCompound();
                        tag.put("pedestal", it);
                        return tag;
                    })
                    .collect(Collectors.toCollection(NbtList::new));
            nbt.put(CommonKeys.NON_EMPTY_PEDESTALS, nbtList);
        } else {
            nbt.remove(CommonKeys.NON_EMPTY_PEDESTALS);
        }

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
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registryLookup);

        nonEmptyPedestalPositions.clear();
        if (nbt.contains(CommonKeys.NON_EMPTY_PEDESTALS, NbtElement.LIST_TYPE)) {
            var nbtList = nbt.getList(CommonKeys.NON_EMPTY_PEDESTALS, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < nbtList.size(); i++) {
                NbtHelper.toBlockPos(nbtList.getCompound(i), "pedestal").ifPresent(nonEmptyPedestalPositions::add);
            }
        }

        currentRecipeId = null;
        if (nbt.contains(CommonKeys.CURRENT_RECIPE, NbtElement.STRING_TYPE)) {
            currentRecipeId = Identifier.tryParse(nbt.getString(CommonKeys.CURRENT_RECIPE));
        }

        remainingCraftingTime = 0;
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
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // OTHER

    public record Progress(ItemStack currentlyCrafted, int remainingCraftingTime, int passedCraftingTime, float value) {
    }
}