package falseresync.wizcraft.client;

import net.fabricmc.api.*;
import net.minecraft.client.network.*;
import net.minecraft.client.world.*;
import net.minecraft.particle.*;
import net.minecraft.util.math.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public class BlockPatternTip {
    public static void spawnCompletionTipParticles(ClientPlayerEntity player, ClientWorld world, List<BlockPos> missingBlocks) {
        for (var missingBlock : missingBlocks) {
            var pos = missingBlock.toCenterPos();
            for (double i = 0; i < world.random.nextBetween(5, 10); i++) {
                world.addParticle(ParticleTypes.GLOW, pos.getX(), pos.getY() - i / 5, pos.getZ(), 0, 0, 0);
            }
        }
    }
}