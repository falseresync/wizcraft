package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WorktableBlockEntity extends BlockEntity {
    public WorktableBlockEntity(BlockEntityType<? extends WorktableBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract SimpleInventory getInventory();

    public abstract InventoryStorage getStorage();

    public abstract void activate(PlayerEntity player);

    public abstract void remove(World world, BlockPos pos);

    public boolean shouldExchangeFor(ItemStack stack) {
        return !stack.isOf(WizcraftItems.WAND);
    }

    public boolean canBeActivatedBy(ItemStack stack) {
        return stack.isOf(WizcraftItems.WAND);
    }
}
