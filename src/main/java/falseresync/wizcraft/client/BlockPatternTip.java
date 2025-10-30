package falseresync.wizcraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BlockPatternTip {
    public static void spawnCompletionTipParticles(LocalPlayer player, ClientLevel world, List<BlockPos> missingBlocks) {
        for (var missingBlock : missingBlocks) {
            var pos = missingBlock.getCenter();
            for (double i = 0; i < world.random.nextIntBetweenInclusive(5, 10); i++) {
                world.addParticle(ParticleTypes.GLOW, pos.x(), pos.y() - i / 5, pos.z(), 0, 0, 0);
            }
        }
    }
}