package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.item.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

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
        return stack.isIn(WizcraftItemTags.WANDS);
    }
}
