package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.common.block.WizBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizBlockEntities {
    public static final BlockEntityType<WorktableBlockEntity> WORKTABLE;
    public static final BlockEntityType<LensingPedestalBlockEntity> LENSING_PEDESTAL;
    private static final Map<Identifier, BlockEntityType<?>> TO_REGISTER = new HashMap<>();

    static {
        WORKTABLE = r(WizBlocks.WORKTABLE.getId(), FabricBlockEntityTypeBuilder
                .create(WorktableBlockEntity::new, WizBlocks.WORKTABLE)
                .build());
        LENSING_PEDESTAL = r(WizBlocks.LENSING_PEDESTAL.getId(), FabricBlockEntityTypeBuilder
                .create(LensingPedestalBlockEntity::new, WizBlocks.LENSING_PEDESTAL)
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
