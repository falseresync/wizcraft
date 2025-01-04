package falseresync.wizcraft.networking.report;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;

public class CometWarpTeleportedReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.ENTITY_PLAYER_TELEPORT);
    }
}
