package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.common.block.WizcraftBlocks;
import dev.falseresync.wizcraft.common.block.entity.worktable.ChargingWorktableBlockEntity;
import dev.falseresync.wizcraft.common.block.entity.worktable.CraftingWorktableBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizcraftBlockEntities {
    public static final BlockEntityType<CraftingWorktableBlockEntity> CRAFTING_WORKTABLE;
    public static final BlockEntityType<ChargingWorktableBlockEntity> CHARGING_WORKTABLE;
    public static final BlockEntityType<LensingPedestalBlockEntity> LENSING_PEDESTAL;
    private static final Map<Identifier, BlockEntityType<?>> TO_REGISTER = new HashMap<>();

    static {
        CRAFTING_WORKTABLE = r(WizcraftBlocks.CRAFTING_WORKTABLE.getId(), FabricBlockEntityTypeBuilder
                .create(CraftingWorktableBlockEntity::new, WizcraftBlocks.CRAFTING_WORKTABLE)
                .build());
        CHARGING_WORKTABLE = r(WizcraftBlocks.CHARGING_WORKTABLE.getId(), FabricBlockEntityTypeBuilder
                .create(ChargingWorktableBlockEntity::new, WizcraftBlocks.CHARGING_WORKTABLE)
                .build());
        LENSING_PEDESTAL = r(WizcraftBlocks.LENSING_PEDESTAL.getId(), FabricBlockEntityTypeBuilder
                .create(LensingPedestalBlockEntity::new, WizcraftBlocks.LENSING_PEDESTAL)
                .build());
    }

    private static <T extends BlockEntity> BlockEntityType<T> r(Identifier id, BlockEntityType<T> type) {
        TO_REGISTER.put(id, type);
        return type;
    }

    public static void register(BiConsumer<Identifier, BlockEntityType<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
