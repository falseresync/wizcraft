package dev.falseresync.wizcraft.common.report.focuses;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class TeleportedReport implements Report {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "teleported");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.ENTITY_PLAYER_TELEPORT);
    }
}
