package falseresync.wizcraft.common;

import net.minecraft.entity.player.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Reports {
    public static void insufficientCharge(PlayerEntity player) {
        player.playSoundToPlayer(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1f, 1f);
        player.sendMessage(Text.translatable("hud.wizcraft.wand.insufficient_charge").formatted(Formatting.DARK_RED), true);
    }

    public static void playSoundToEveryone(PlayerEntity player, SoundEvent sound) {
        player.playSound(sound);
        player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1f, 1f);
    }

    public static void addSparkles(World world, Vec3d pos) {
        addParticle(world, ParticleTypes.FIREWORK, pos, 5, 10);
    }

    public static void addSmoke(World world, Vec3d pos) {
        addParticle(world, ParticleTypes.WHITE_SMOKE, pos, 5, 10);
    }

    public static void addParticle(World world, ParticleEffect parameters, Vec3d pos, int minAmount, int maxAmount) {
        var random = world.getRandom();
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    parameters, pos.x, pos.y, pos.z,
                    random.nextBetween(minAmount, maxAmount),
                    (random.nextFloat() - 0.5) / 2,
                    random.nextFloat() / 2,
                    (random.nextFloat() - 0.5) / 2,
                    0.15);
        } else {
            for (int i = 0; i < random.nextBetween(minAmount, maxAmount); i++) {
                world.addParticle(
                        parameters, pos.x, pos.y, pos.z,
                        (random.nextFloat() - 0.5) / 2,
                        random.nextFloat() / 2,
                        (random.nextFloat() - 0.5) / 2);
            }
        }
    }
}
