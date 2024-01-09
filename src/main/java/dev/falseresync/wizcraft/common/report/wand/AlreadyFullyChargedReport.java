package dev.falseresync.wizcraft.common.report.wand;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.client.hud.WizHud;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AlreadyFullyChargedReport implements Report {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "wand/already_fully_charged");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
        WizHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.wand.already_charged"));
    }
}
