package dev.falseresync.wizcraft.common.report.focuses;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class AnchorPlacedReport implements Report {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "anchor_placed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK);
    }
}
