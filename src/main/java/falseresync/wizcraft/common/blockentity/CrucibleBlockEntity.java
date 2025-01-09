package falseresync.wizcraft.common.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CrucibleBlockEntity extends BlockEntity {
    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(WizcraftBlockEntities.CRUCIBLE, pos, state);
    }
}
