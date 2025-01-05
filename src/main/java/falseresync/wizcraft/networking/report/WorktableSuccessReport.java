package falseresync.wizcraft.networking.report;

import falseresync.wizcraft.common.WizcraftSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;


public class WorktableSuccessReport implements MultiplayerReport {
    @Override
    public void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        world.playSound(null, pos, WizcraftSounds.WORKTABLE_SUCCESS, SoundCategory.BLOCKS, 1f, 1f);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnNearbyClients(ClientPlayerEntity player) {
        MinecraftClient.getInstance().getSoundManager().stopSounds(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value().getId(), SoundCategory.BLOCKS);
    }
}
