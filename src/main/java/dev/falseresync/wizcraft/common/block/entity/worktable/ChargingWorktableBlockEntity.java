package dev.falseresync.wizcraft.common.block.entity.worktable;

import dev.falseresync.wizcraft.api.common.worktable.WorktableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChargingWorktableBlockEntity extends WorktableBlockEntity {
    public ChargingWorktableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public SimpleInventory getInventory() {
        return null;
    }

    @Override
    public InventoryStorage getStorage() {
        return null;
    }

    @Override
    public void interact(PlayerEntity player) {

    }

    @Override
    public void remove(World world, BlockPos pos) {

    }
}
