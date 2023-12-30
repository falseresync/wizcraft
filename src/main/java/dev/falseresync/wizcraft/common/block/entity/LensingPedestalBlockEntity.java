package dev.falseresync.wizcraft.common.block.entity;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.BlockPos;

public class LensingPedestalBlockEntity extends BlockEntity {
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public void markDirty() {
            LensingPedestalBlockEntity.this.markDirty();
        }
    };
    public final InventoryStorage storage = InventoryStorage.of(this.inventory, null);

    public LensingPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.LENSING_PEDESTAL, pos, state);
    }
}
