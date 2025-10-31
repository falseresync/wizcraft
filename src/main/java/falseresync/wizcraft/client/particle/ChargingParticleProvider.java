package falseresync.wizcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class ChargingParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public ChargingParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        var glowParticle = new GlowParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
        if (level.random.nextBoolean()) {
            glowParticle.setColor(0.6F, 1.0F, 0.8F);
        } else {
            glowParticle.setColor(0.08F, 0.4F, 0.4F);
        }

        glowParticle.setLifetime((int) (16.0 / (level.random.nextDouble() * 0.8 + 0.2)));
        return glowParticle;
    }
}
