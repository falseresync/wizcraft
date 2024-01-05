package dev.falseresync.wizcraft.common.report.skywand;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetInstancePriority;
import dev.falseresync.wizcraft.api.common.report.CommonReport;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.report.ReportUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SuccessfullyChargedReport implements CommonReport {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "successfully_charged");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, 1F, 1.25F);
        WizHud.STATUS_MESSAGE.override(
                Text.translatable("hud.wizcraft.sky_wand.successfully_charged").styled(style -> style.withColor(Formatting.GOLD)),
                WidgetInstancePriority.HIGH);
    }

    @Override
    public void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        if (source == null) return;
        var rotation = source.getRotationVec(1);
        var orthogonalDistance = 1;
        var sourcePos = source.getEyePos()
                .add(source.getHandPosOffset(WizItems.SKY_WAND))
                .add(rotation.x * orthogonalDistance, -0.25, rotation.z * orthogonalDistance);
        ReportUtil.addFireworkSparkles(world, sourcePos);
    }
}
