package dev.falseresync.wizcraft.common.entity;

import dev.falseresync.wizcraft.common.sound.WizSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class StarProjectileEntity extends ExplosiveProjectileEntity {
    public static final StarProjectileEntity.StarProjectileExplosionBehavior EXPLOSION_BEHAVIOR = new StarProjectileExplosionBehavior();

    /**
     * So, a bit of an explanation. If I try to remove the entity on the same tick as the collision
     * EVERYTHING breaks. I have no idea why. I guess world is processing particles/sounds/etc weirdly.
     * I couldn't find any reason as to why Dragon can do it on the same tick, but I don't
     * Explanations in a GitHub issue are welcome, if someone sees this rant.
     */
//    protected boolean shouldDiscardNextTick = false;
    public StarProjectileEntity(EntityType<StarProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public StarProjectileEntity(LivingEntity owner, World world) {
        this(WizEntities.STAR_PROJECTILE, world);
        setOwner(owner);
        var rotation = owner.getRotationVec(1);
        var orthogonalDistance = 1;
        setPosition(owner.getX() + rotation.x * orthogonalDistance, owner.getEyeY(), owner.getZ() + rotation.z * orthogonalDistance);
        setVelocity(owner, owner.getPitch(), owner.getYaw(), 0, 1.5F, 1F);
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.ELECTRIC_SPARK;
    }

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient && isAlive() && getOwner() != null && getVelocity().lengthSquared() < 0.25) {
            explode();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!getWorld().isClient) {
            var target = entityHitResult.getEntity();
            var owner = getOwner();
            target.damage(getDamageSources().indirectMagic(this, owner), 1.5F);
            if (owner instanceof LivingEntity attacker) {
                applyDamageEffects(attacker, target);
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!getWorld().isClient) {
            explode();
        }
    }

    protected void explode() {
        getWorld().createExplosion(
                this, getDamageSources().indirectMagic(this, getOwner()),
                EXPLOSION_BEHAVIOR, getX(), getY(), getZ(), 1f, false, World.ExplosionSourceType.NONE,
                ParticleTypes.FLAME, ParticleTypes.EXPLOSION_EMITTER, WizSounds.Entity.STAR_PROJECTILE_EXPLODE);
        discard();
    }

    public static class StarProjectileExplosionBehavior extends ExplosionBehavior {
        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}
