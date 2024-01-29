package dev.falseresync.wizcraft.common.report.worktable;

import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.report.ReportUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class WorktableInterruptedReport implements MultiplayerReport {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "worktable/interrupted");
    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        world.playSound(null, pos, SoundEvents.ENTITY_HORSE_BREATHE, SoundCategory.BLOCKS, 1f, 1f);
        ReportUtils.addSmoke(world, pos.toCenterPos().add(0, 0.75, 0));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnNearbyClients(ClientPlayerEntity player) {
        MinecraftClient.getInstance().getSoundManager().stopSounds(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value().getId(), SoundCategory.BLOCKS);
    }
}
