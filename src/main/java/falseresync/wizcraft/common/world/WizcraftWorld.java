package falseresync.wizcraft.common.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class WizcraftWorld {
    public static class MagicDischargeExplosionBehavior extends ExplosionBehavior {
        public static final MagicDischargeExplosionBehavior INSTANCE = new MagicDischargeExplosionBehavior();

        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}
