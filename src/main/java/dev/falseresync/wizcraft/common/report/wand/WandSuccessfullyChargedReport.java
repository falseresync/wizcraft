package dev.falseresync.wizcraft.common.report.wand;

import dev.falseresync.wizcraft.api.WizcraftApi;
import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import dev.falseresync.wizcraft.client.hud.WizcraftHud;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.common.report.ReportUtils;
import dev.falseresync.wizcraft.common.WizcraftSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class WandSuccessfullyChargedReport implements MultiplayerReport {
    public static final Identifier ID = wid("wand/successfully_charged");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSound(WizcraftSounds.Focus.SUCCESSFULLY_CHARGED, 1, 1);
        WizcraftApi.getHud().getMessageDisplay().postImportant(Text.translatable("hud.wizcraft.wand.successfully_charged").styled(style -> style.withColor(Formatting.GOLD)));
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
