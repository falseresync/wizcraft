package falseresync.wizcraft.networking.report;

import falseresync.wizcraft.common.WizcraftSounds;
import net.minecraft.client.network.ClientPlayerEntity;

public class CometWarpAnchorPlacedReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(WizcraftSounds.COMET_WARP_ANCHOR_PLACED);
    }
}
