package falseresync.wizcraft.networking.report;

import falseresync.wizcraft.networking.s2c.TriggerMultiplayerReportS2CPacket;
import falseresync.wizcraft.networking.s2c.TriggerReportS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface MultiplayerReport extends Report {
    default void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {}

    @Environment(EnvType.CLIENT)
    default void executeOnNearbyClients(ClientPlayerEntity player) {}

    static void trigger(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source, MultiplayerReport report) {
        if (source != null) {
            ServerPlayNetworking.send(source, new TriggerReportS2CPacket(report));
        }
        for (var player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, new TriggerMultiplayerReportS2CPacket(report));
        }
        report.executeOnServer(world, pos, source);
    }
}
