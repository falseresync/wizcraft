package dev.falseresync.wizcraft.common.report.focuses.cometwarp;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class CometWarpTeleportedReport implements Report {
    public static final Identifier ID = wid("focus/comet_warp/teleported");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.ENTITY_PLAYER_TELEPORT);
    }
}
