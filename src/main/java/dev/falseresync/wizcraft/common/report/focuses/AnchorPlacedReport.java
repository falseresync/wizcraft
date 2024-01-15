package dev.falseresync.wizcraft.common.report.focuses;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.sound.WizSounds;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

public class AnchorPlacedReport implements Report {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "focus/anchor_placed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(WizSounds.Focus.COMET_WARP_ANCHOR_PLACED);
    }
}