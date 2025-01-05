package falseresync.wizcraft.networking.report;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class WorktableCraftingReport implements MultiplayerReport {
    @Override
    public void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        world.playSound(null, pos, SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value(), SoundCategory.BLOCKS, 1f, 1f);
    }
}
