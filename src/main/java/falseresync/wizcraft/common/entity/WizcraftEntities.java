package falseresync.wizcraft.common.entity;

import falseresync.lib.registry.RegistryObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class WizcraftEntities {
    public static final @RegistryObject EntityType<StarProjectileEntity> STAR_PROJECTILE = EntityType.Builder
            .<StarProjectileEntity>create(StarProjectileEntity::new, SpawnGroup.MISC)
            .dimensions(0.5F, 0.5F)
            .makeFireImmune()
            .disableSaving()
            .maxTrackingRange(16)
            .build();
}
