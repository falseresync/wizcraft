package falseresync.wizcraft.common.world;

import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;

public class WizcraftWorld {
    public static class MagicDischargeExplosionBehavior extends ExplosionBehavior {
        public static final MagicDischargeExplosionBehavior INSTANCE = new MagicDischargeExplosionBehavior();

        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}
