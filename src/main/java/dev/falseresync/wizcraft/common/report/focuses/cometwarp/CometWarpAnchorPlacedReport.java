package dev.falseresync.wizcraft.common.report.focuses.cometwarp;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.WizcraftSounds;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

public class CometWarpAnchorPlacedReport implements Report {
    public static final Identifier ID = new Identifier(Wizcraft.MOD_ID, "focus/comet_warp/anchor_placed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(WizcraftSounds.Focus.COMET_WARP_ANCHOR_PLACED);
    }
}
