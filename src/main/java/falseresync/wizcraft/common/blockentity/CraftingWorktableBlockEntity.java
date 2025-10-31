package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.CommonKeys;
import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.recipe.LensedWorktableRecipe;
import falseresync.wizcraft.common.recipe.SimpleInventoryRecipeInput;
import falseresync.wizcraft.common.recipe.WizcraftRecipes;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CraftingWorktableBlockEntity extends WorktableBlockEntity {
    public static final int IDLE_SEARCH_COOLDOWN = 5;
    protected final List<LensingPedestalBlockEntity> pedestals = new ArrayList<>();
    protected final List<BlockPos> nonEmptyPedestalPositions = new ArrayList<>();
    protected final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected final SimpleInventoryRecipeInput virtualCombinedInventory = new SimpleInventoryRecipeInput(5) {
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    protected int remainingIdleSearchCooldown = 0;
    protected int remainingCraftingTime = 0;
    protected int craftingTime = 0;
    protected ItemStack currentlyCrafted = ItemStack.EMPTY;
    protected @Nullable ResourceLocation currentRecipeId;
    protected @Nullable LensedWorktableRecipe currentRecipe;

    public CraftingWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.CRAFTING_WORKTABLE, pos, state);
        inventory.addListener(sender -> setChanged());
    }

    // PUBLIC INTERFACE

    public static void tick(Level world, BlockPos pos, BlockState state, CraftingWorktableBlockEntity worktable) {
        worktable.tick(world, pos, state);
    }

    @Override
    public void activate(@Nullable Player player) {
        if (level == null || level.isClientSide()) return;

        searchPedestals(level, worldPosition);
        if (pedestals.size() < 4) {
            if (player instanceof ServerPlayer serverPlayer) {
                player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1f, 1f);
                player.displayClientMessage(Component.translatable("hud.wizcraft.worktable.incomplete_worktable"), true);
            }
            return;
        }

        updateVirtualCombinedInventory();
        level.getRecipeManager()
                .getRecipeFor(WizcraftRecipes.LENSED_WORKTABLE, virtualCombinedInventory, level)
                .ifPresent(this::beginCrafting);
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        searchPedestals(world, pos);
        pedestals.forEach(pedestal -> pedestal.linkTo(null));
    }

    public List<BlockPos> getNonEmptyPedestalPositions() {
        return nonEmptyPedestalPositions;
    }

    public Progress getProgress() {
        return new Progress(
                currentlyCrafted,
                remainingCraftingTime,
                craftingTime - remainingCraftingTime,
                1 - (float) remainingCraftingTime / craftingTime);
    }

    public ItemStack getHeldStackCopy() {
        return inventory.getItem(0).copy();
    }

    @Override
    public SimpleContainer getInventory() {
        return inventory;
    }

    // TICKERS

    @Override
    public InventoryStorage getStorage() {
        return storage;
    }

    protected void tick(Level world, BlockPos pos, BlockState state) {
        if (currentRecipe == null && currentRecipeId != null) {
            updateVirtualCombinedInventory();
            world.getRecipeManager().byKey(currentRecipeId)
                    .flatMap(entry -> entry.value() instanceof LensedWorktableRecipe recipe
                            ? Optional.of(recipe)
                            : Optional.empty())
                    .ifPresentOrElse(recipe -> initStaticRecipeData(world, recipe), this::reset);
        }

        if (world.isClientSide) return;

        if (currentRecipe != null) {
            tickCrafting(world, pos, currentRecipe);
            return;
        }

        tickIdle(world, pos);
    }

    protected void tickCrafting(Level world, BlockPos pos, LensedWorktableRecipe recipe) {
        remainingCraftingTime -= 1;
        searchPedestals(world, pos);
        setChanged();
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

    protected void tickIdle(Level world, BlockPos pos) {
        if (remainingIdleSearchCooldown > 0) {
            remainingIdleSearchCooldown -= 1;
            return;
        }

        remainingIdleSearchCooldown = IDLE_SEARCH_COOLDOWN;
        searchPedestals(world, pos);
    }

    // DATA-MUTATING INTERNALS

    protected void searchPedestals(Level world, BlockPos pos) {
        pedestals.clear();

        for (var pedestalPos : List.of(pos.north(2), pos.west(2), pos.south(2), pos.east(2))) {
            if (world.getBlockEntity(pedestalPos) instanceof LensingPedestalBlockEntity pedestal) {
                if (!pedestal.isLinkedTo(this)) {
                    world.destroyBlock(pos, true);
                    world.playSound(null, pos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 1, 1);
                } else {
                    pedestals.add(pedestal);
                    pedestal.linkTo(this);
                }
            }
        }
    }

    protected void updateVirtualCombinedInventory() {
        virtualCombinedInventory.setItem(0, inventory.getItem(0).copy());
        for (int i = 0; i < pedestals.size(); i++) {
            virtualCombinedInventory.setItem(i + 1, pedestals.get(i).getHeldStackCopy());
        }
    }

    protected void beginCrafting(RecipeHolder<LensedWorktableRecipe> recipeEntry) {
        if (level == null || level.isClientSide()) return;

        currentRecipeId = recipeEntry.id();
        initStaticRecipeData(level, recipeEntry.value());
        remainingCraftingTime = recipeEntry.value().getCraftingTime();
        setChanged();

        level.playSound(null, worldPosition, SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value(), SoundSource.BLOCKS, 1f, 1f);
    }

    protected void interruptCrafting() {
        if (level == null || level.isClientSide()) return;

        reset();
        onInterrupted(this, level, worldPosition);
    }

    protected void finishCrafting() {
        if (level == null || level.isClientSide() || currentRecipe == null) return;

        inventory.setItem(0, currentRecipe.assemble(virtualCombinedInventory, level.registryAccess()));
        var remainders = currentRecipe.getRemainingItems(virtualCombinedInventory);
        for (int i = 0; i < pedestals.size(); i++) {
            pedestals.get(i).onCrafted(remainders.get(i + 1));
        }

        reset();
        level.playSound(null, worldPosition, WizcraftSounds.WORKTABLE_SUCCESS, SoundSource.BLOCKS, 1f, 1f);
        var stopSoundPacket = new ClientboundStopSoundPacket(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value().getLocation(), SoundSource.BLOCKS);
        for (var player : PlayerLookup.tracking(this)) {
            player.connection.send(stopSoundPacket);
        }
    }


    protected void initStaticRecipeData(Level world, LensedWorktableRecipe recipe) {
        currentRecipe = recipe;
        craftingTime = recipe.getCraftingTime();
        currentlyCrafted = currentRecipe.assemble(virtualCombinedInventory, world.registryAccess());
    }

    protected void reset() {
        virtualCombinedInventory.clearContent();
        currentRecipeId = null;
        currentRecipe = null;
        craftingTime = 0;
        currentlyCrafted = ItemStack.EMPTY;
        remainingCraftingTime = 0;
        remainingIdleSearchCooldown = 0;

        setChanged();
    }

    // DATA-SAVING INTERNALS

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);

        ContainerHelper.saveAllItems(nbt, inventory.getItems(), registryLookup);

        if (!pedestals.isEmpty()) {
            var nbtList = pedestals.stream()
                    .filter(pedestal -> !pedestal.getHeldStackCopy().isEmpty())
                    .map(BlockEntity::getBlockPos)
                    .map(NbtUtils::writeBlockPos)
                    .map(it -> {
                        var tag = new CompoundTag();
                        tag.put("pedestal", it);
                        return tag;
                    })
                    .collect(Collectors.toCollection(ListTag::new));
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
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        inventory.getItems().clear();
        ContainerHelper.loadAllItems(nbt, inventory.getItems(), registryLookup);

        nonEmptyPedestalPositions.clear();
        if (nbt.contains(CommonKeys.NON_EMPTY_PEDESTALS, Tag.TAG_LIST)) {
            var nbtList = nbt.getList(CommonKeys.NON_EMPTY_PEDESTALS, Tag.TAG_COMPOUND);
            for (int i = 0; i < nbtList.size(); i++) {
                NbtUtils.readBlockPos(nbtList.getCompound(i), "pedestal").ifPresent(nonEmptyPedestalPositions::add);
            }
        }

        currentRecipeId = null;
        if (nbt.contains(CommonKeys.CURRENT_RECIPE, Tag.TAG_STRING)) {
            currentRecipeId = ResourceLocation.tryParse(nbt.getString(CommonKeys.CURRENT_RECIPE));
        }

        remainingCraftingTime = 0;
        if (nbt.contains(CommonKeys.REMAINING_CRAFTING_TIME, Tag.TAG_INT)) {
            remainingCraftingTime = nbt.getInt(CommonKeys.REMAINING_CRAFTING_TIME);
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

    // OTHER

    public record Progress(ItemStack currentlyCrafted, int remainingCraftingTime, int passedCraftingTime, float value) {
    }
}