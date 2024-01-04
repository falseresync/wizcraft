package dev.falseresync.wizcraft.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class StarProjectileEntity extends ExplosiveProjectileEntity {
    /**
     * So, a bit of an explanation. If I try to remove the entity on the same tick as the collision
     * EVERYTHING breaks. I have no idea why. I guess world is processing particles/sounds/etc weirdly.
     * I couldn't find any reason as to why Dragon can do it on the same tick, but I don't
     * Explanations in a GitHub issue are welcome, if someone sees this rant.
     */
    protected boolean shouldDiscardNextTick = false;

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
        if (!getWorld().isClient() && shouldDiscardNextTick) {
            discard();
        }
        super.tick();
        if (getVelocity().lengthSquared() < 0.25 && getOwner() != null) {
            pop();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!getWorld().isClient) {
            Entity other = entityHitResult.getEntity();
            Entity owner = getOwner();
            other.damage(getDamageSources().indirectMagic(this, owner), 3.0F);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        pop();
    }

    protected void pop() {
        getWorld().playSound(getOwner() instanceof PlayerEntity player ? player : null,
                getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.75F, 1.25F);

        var random = getWorld().getRandom();
        for (int i = 0; i < random.nextBetween(5, 10); i++) {
            getWorld().addParticle(
                    ParticleTypes.FIREWORK,
                    getParticleX(0.5), getRandomBodyY(), getParticleZ(0.5),
                    (random.nextFloat() - 0.5) / 2,
                    random.nextFloat() / 2,
                    (random.nextFloat() - 0.5) / 2);
        }

        shouldDiscardNextTick = true;
    }
}
