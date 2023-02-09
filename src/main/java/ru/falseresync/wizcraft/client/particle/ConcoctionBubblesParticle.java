package ru.falseresync.wizcraft.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class ConcoctionBubblesParticle extends SpriteBillboardParticle {
    protected ConcoctionBubblesParticle(ClientWorld clientWorld, double x, double y, double z, FabricSpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, 0, 0, 0);
        setSprite(spriteProvider);

        velocityX *= 0.3;
        velocityY = Math.random() * 0.1 + 0.1;
        velocityZ *= 0.3;
        maxAge = (int) (4.0 / (Math.random() * 0.8 + 0.2));
        scale += (Math.random() - 0.8) * 0.1;
    }

    @Override
    public void tick() {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        
        if (maxAge-- <= 0) {
            markDead();
            return;
        }

        move(velocityX, velocityY, velocityZ);
        
        velocityX *= 0.98;
        velocityY *= 0.7;
        velocityZ *= 0.98;

        if (onGround) {
            if (Math.random() < 0.5) {
                markDead();
            }
            velocityX *= 0.7;
            velocityZ *= 0.7;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final FabricSpriteProvider spriteProvider;

        public Factory(FabricSpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ConcoctionBubblesParticle(world, x, y, z, spriteProvider);
        }
    }
}
