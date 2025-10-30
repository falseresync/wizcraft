package falseresync.wizcraft.common.entity;

import falseresync.lib.registry.RegistryObject;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class WizcraftEntities {
    public static final @RegistryObject EntityType<StarProjectileEntity> STAR_PROJECTILE = EntityType.Builder
            .<StarProjectileEntity>of(StarProjectileEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .fireImmune()
            .noSave()
            .clientTrackingRange(16)
            .build();
    public static final @RegistryObject EntityType<EnergyVeilEntity> ENERGY_VEIL = EntityType.Builder
            .<EnergyVeilEntity>of(EnergyVeilEntity::new, MobCategory.MISC)
            .sized(0F, 0F)
            .fireImmune()
            .noSave()
            .clientTrackingRange(16)
            .build();
}
