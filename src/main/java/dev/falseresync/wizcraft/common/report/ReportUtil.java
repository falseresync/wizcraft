package dev.falseresync.wizcraft.common.report;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ReportUtil {
    public static void addSparkles(World world, Vec3d pos) {
        addParticle(ParticleTypes.FIREWORK, world, pos, 5, 10);
    }

    public static void addSmoke(World world, Vec3d pos) {
        addParticle(ParticleTypes.WHITE_SMOKE, world, pos, 5, 10);
    }

    public static void addParticle(ParticleEffect particle, World world, Vec3d pos, int minAmount, int maxAmount) {
        var random = world.getRandom();
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    particle, pos.x, pos.y, pos.z,
                    random.nextBetween(minAmount, maxAmount),
                    (random.nextFloat() - 0.5) / 2,
                    random.nextFloat() / 2,
                    (random.nextFloat() - 0.5) / 2,
                    0.15);
        } else {
            for (int i = 0; i < random.nextBetween(minAmount, maxAmount); i++) {
                world.addParticle(
                        particle, pos.x, pos.y, pos.z,
                        (random.nextFloat() - 0.5) / 2,
                        random.nextFloat() / 2,
                        (random.nextFloat() - 0.5) / 2);
            }
        }
    }
}
