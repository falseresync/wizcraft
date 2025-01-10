package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.recipe.CrucibleRecipe;
import falseresync.wizcraft.common.recipe.SimpleInventoryRecipeInput;
import falseresync.wizcraft.common.recipe.WizcraftRecipeTypes;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


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

    public SimpleInventory getInventory() {
        return inventory;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if (world.isClient) return;

        world.getEntitiesByClass(ItemEntity.class, Box.enclosing(pos, pos.up()).contract(2 / 16f,0.75, 2 / 16f), entity -> true).forEach(entity -> {
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

        world.getRecipeManager().getFirstMatch(WizcraftRecipeTypes.CRUCIBLE, crucible.inventory, world).ifPresent(crucible::craft);
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
