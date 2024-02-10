package dev.falseresync.wizcraft.common.report.focuses.cometwarp;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.client.hud.WizcraftHud;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CometWarpNoAnchorReport implements Report {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "focus/comet_warp/no_anchor");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        WizcraftHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.wand.no_anchor"));
    }
}
