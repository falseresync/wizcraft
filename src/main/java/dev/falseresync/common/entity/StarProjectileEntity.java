package dev.falseresync.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class StarProjectileEntity extends ExplosiveProjectileEntity {
    public StarProjectileEntity(EntityType<StarProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public StarProjectileEntity(LivingEntity owner, double directionX, double directionY, double directionZ, World world) {
        super(WizEntityTypes.STAR_PROJECTILE, owner, directionX, directionY, directionZ, world);
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.ELECTRIC_SPARK;
    }
}
