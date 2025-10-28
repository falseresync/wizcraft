package falseresync.wizcraft.common.blockentity;

import falseresync.lib.registry.RegistryObject;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import net.minecraft.block.entity.BlockEntityType;

public class WizcraftBlockEntities {
    public static final @RegistryObject BlockEntityType<CrucibleBlockEntity> CRUCIBLE =
            BlockEntityType.Builder.create(CrucibleBlockEntity::new, WizcraftBlocks.CRUCIBLE).build();

    public static final @RegistryObject BlockEntityType<LensBlockEntity> LENS =
            BlockEntityType.Builder.create(LensBlockEntity::new, WizcraftBlocks.LENS).build();
    public static final @RegistryObject BlockEntityType<LensingPedestalBlockEntity> LENSING_PEDESTAL =
            BlockEntityType.Builder.create(LensingPedestalBlockEntity::new, WizcraftBlocks.LENSING_PEDESTAL).build();

    public static final @RegistryObject BlockEntityType<CraftingWorktableBlockEntity> CRAFTING_WORKTABLE =
            BlockEntityType.Builder.create(CraftingWorktableBlockEntity::new, WizcraftBlocks.CRAFTING_WORKTABLE).build();
    public static final @RegistryObject BlockEntityType<ChargingWorktableBlockEntity> CHARGING_WORKTABLE =
            BlockEntityType.Builder.create(ChargingWorktableBlockEntity::new, WizcraftBlocks.CHARGING_WORKTABLE).build();
}
