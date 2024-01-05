package dev.falseresync.wizcraft.api.common.report;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.network.s2c.TriggerReportS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface Report extends HasId {
    @Environment(EnvType.CLIENT)
    void execute(ClientPlayerEntity player);

    static void trigger(ServerPlayerEntity player, Report report) {
        ServerPlayNetworking.send(player, new TriggerReportS2CPacket(report));
    }
}
