package falseresync.wizcraft.networking.report;

import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class WandSuccessfullyChargedReport implements MultiplayerReport {

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSound(WizcraftSounds.SUCCESSFULLY_CHARGED, 1, 1);
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.translatable("hud.wizcraft.wand.successfully_charged").styled(style -> style.withColor(Formatting.GOLD)), false);
    }

    @Override
    public void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        if (source == null) return;
        var rotation = source.getRotationVec(1);
        var orthogonalDistance = 1;
        var sourcePos = source.getEyePos()
                .add(source.getHandPosOffset(WizcraftItems.WAND))
                .add(rotation.x * orthogonalDistance, -0.25, rotation.z * orthogonalDistance);
        ReportUtils.addSparkles(world, sourcePos);
    }
}
