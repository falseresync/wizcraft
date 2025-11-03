package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.recipe.CrucibleRecipe;
import falseresync.wizcraft.common.recipe.SimpleInventoryRecipeInput;
import falseresync.wizcraft.common.recipe.WizcraftRecipes;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;


public class CrucibleBlockEntity extends BlockEntity {
    protected final SimpleInventoryRecipeInput inventory = new SimpleInventoryRecipeInput(5) {
        @Override
        public int getMaxStackSize() {
            return 10;
        }
    };

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.CRUCIBLE, pos, state);
        inventory.addListener(change -> setChanged());
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if (world.isClientSide) return;

        world.getEntitiesOfClass(ItemEntity.class, AABB.encapsulatingFullBlocks(pos, pos.above()).deflate(2 / 16f, 0.75, 2 / 16f), entity -> true).forEach(entity -> {
            var stack = entity.getItem();
            var inventory = crucible.inventory;
            var max = inventory.getMaxStackSize(stack);
            // If there's too many items, they get lost
            if (inventory.hasAnyMatching(contained -> ItemStack.isSameItemSameComponents(contained, stack) && contained.getCount() + stack.getCount() > max) || stack.getCount() > max) {
                inventory.removeItemType(stack.getItem(), max);
                inventory.addItem(stack.copyWithCount(stack.getCount() - max));
                onInterrupted(crucible, world, pos);
            } else if (!inventory.addItem(stack.copy()).isEmpty()) {
                inventory.removeItemNoUpdate(0);
                inventory.setItem(0, inventory.getItem(1));
                inventory.setItem(1, inventory.getItem(2));
                inventory.setItem(2, inventory.getItem(3));
                inventory.setItem(3, inventory.getItem(4));
                onInterrupted(crucible, world, pos);
            }
            entity.discard();
        });

        world.getRecipeManager().getRecipeFor(WizcraftRecipes.CRUCIBLE, crucible.inventory.recipeInput(), world).ifPresent(crucible::craft);
    }

    private static void onInterrupted(CrucibleBlockEntity crucible, Level world, BlockPos pos) {
        // TODO: bad effects
        world.playSound(null, pos, SoundEvents.HORSE_BREATHE, SoundSource.BLOCKS, 1f, 1f);
        Reports.addSmoke(world, pos.getCenter().add(0, 0.75, 0));
        var stopSoundPacket = new ClientboundStopSoundPacket(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value().getLocation(), SoundSource.BLOCKS);
        for (var player : PlayerLookup.tracking(crucible)) {
            player.connection.send(stopSoundPacket);
        }
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public void craft(RecipeHolder<CrucibleRecipe> recipeEntry) {
        if (level == null) return;

        var result = recipeEntry.value().assemble(inventory.recipeInput(), level.registryAccess());
        inventory.clearContent();
        var entityPos = worldPosition.getCenter().add(0, 1.25, 0);
        var entity = new ItemEntity(level, entityPos.x, entityPos.y, entityPos.z, result, 0, 0, 0);
        entity.setNoGravity(true);
        level.addFreshEntity(entity);
    }

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
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        inventory.getItems().clear();
        ContainerHelper.loadAllItems(nbt, inventory.getItems(), registryLookup);
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
