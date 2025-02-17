package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.recipe.*;
import falseresync.wizcraft.networking.report.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;


public class CrucibleBlockEntity extends BlockEntity {
    protected final SimpleInventoryRecipeInput inventory = new SimpleInventoryRecipeInput(5) {
        @Override
        public int getMaxCountPerStack() {
            return 10;
        }
    };

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.CRUCIBLE, pos, state);
        inventory.addListener(change -> markDirty());
    }

    public static void tick(World world, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if (world.isClient) return;

        world.getEntitiesByClass(ItemEntity.class, Box.enclosing(pos, pos.up()).contract(2 / 16f, 0.75, 2 / 16f), entity -> true).forEach(entity -> {
            var stack = entity.getStack();
            var inventory = crucible.inventory;
            var max = inventory.getMaxCount(stack);
            // If there's too many items, they get lost
            if (inventory.containsAny(contained -> ItemStack.areItemsAndComponentsEqual(contained, stack) && contained.getCount() + stack.getCount() > max) || stack.getCount() > max) {
                inventory.removeItem(stack.getItem(), max);
                inventory.addStack(stack.copyWithCount(stack.getCount() - max));
                // TODO: bad effects
                WizcraftReports.WORKTABLE_INTERRUPTED.sendAround((ServerWorld) world, pos, null);
            } else if (!inventory.addStack(stack.copy()).isEmpty()) {
                inventory.removeStack(0);
                inventory.setStack(0, inventory.getStackInSlot(1));
                inventory.setStack(1, inventory.getStackInSlot(2));
                inventory.setStack(2, inventory.getStackInSlot(3));
                inventory.setStack(3, inventory.getStackInSlot(4));
                // TODO: bad effects
                WizcraftReports.WORKTABLE_INTERRUPTED.sendAround((ServerWorld) world, pos, null);
            }
            entity.discard();
        });

        world.getRecipeManager().getFirstMatch(WizcraftRecipes.CRUCIBLE, crucible.inventory, world).ifPresent(crucible::craft);
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    public void craft(RecipeEntry<CrucibleRecipe> recipeEntry) {
        if (world == null) return;

        var result = recipeEntry.value().craft(inventory, world.getRegistryManager());
        inventory.clear();
        var entityPos = pos.toCenterPos().add(0, 1.25, 0);
        var entity = new ItemEntity(world, entityPos.x, entityPos.y, entityPos.z, result, 0, 0, 0);
        entity.setNoGravity(true);
        world.spawnEntity(entity);
    }

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
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registryLookup);
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
