package dev.falseresync.wizcraft.api.common.report;

import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface AreaReport extends Report {
    void executeOnArea(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source);

    static void trigger(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source, AreaReport report) {
        if (source != null) {
            ServerPlayNetworking.send(source, new TriggerReportS2CPacket(report));
        }
        report.executeOnArea(world, pos, source);
    }
}
