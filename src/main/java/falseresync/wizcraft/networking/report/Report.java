package falseresync.wizcraft.networking.report;

import falseresync.wizcraft.networking.s2c.TriggerReportS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface Report {
    @Environment(EnvType.CLIENT)
    default void executeOnClient(ClientPlayerEntity player) {}

    static void trigger(ServerPlayerEntity player, Report report) {
        ServerPlayNetworking.send(player, new TriggerReportS2CPacket(report));
    }

    default void sendTo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new TriggerReportS2CPacket(this));
    }
}
