package falseresync.wizcraft.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;

public class WizcraftWorld {
    public static class MagicDischargeExplosionBehavior extends ExplosionDamageCalculator {
        public static final MagicDischargeExplosionBehavior INSTANCE = new MagicDischargeExplosionBehavior();

        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}
