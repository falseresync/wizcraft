package dev.falseresync.wizcraft.api.common.worktable;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WorktableBlockEntity extends BlockEntity {
    public WorktableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract SimpleInventory getInventory();

    public abstract InventoryStorage getStorage();

    public abstract void interact(PlayerEntity player);

    public abstract void remove(World world, BlockPos pos);
}
