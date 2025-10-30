package falseresync.wizcraft.common.blockentity;

import falseresync.lib.registry.RegistryObject;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class WizcraftBlockEntities {
    public static final @RegistryObject BlockEntityType<CrucibleBlockEntity> CRUCIBLE =
            BlockEntityType.Builder.of(CrucibleBlockEntity::new, WizcraftBlocks.CRUCIBLE).build();

    public static final @RegistryObject BlockEntityType<LensBlockEntity> LENS =
            BlockEntityType.Builder.of(LensBlockEntity::new, WizcraftBlocks.LENS).build();
    public static final @RegistryObject BlockEntityType<LensingPedestalBlockEntity> LENSING_PEDESTAL =
            BlockEntityType.Builder.of(LensingPedestalBlockEntity::new, WizcraftBlocks.LENSING_PEDESTAL).build();

    public static final @RegistryObject BlockEntityType<CraftingWorktableBlockEntity> CRAFTING_WORKTABLE =
            BlockEntityType.Builder.of(CraftingWorktableBlockEntity::new, WizcraftBlocks.CRAFTING_WORKTABLE).build();
    public static final @RegistryObject BlockEntityType<ChargingWorktableBlockEntity> CHARGING_WORKTABLE =
            BlockEntityType.Builder.of(ChargingWorktableBlockEntity::new, WizcraftBlocks.CHARGING_WORKTABLE).build();
}
