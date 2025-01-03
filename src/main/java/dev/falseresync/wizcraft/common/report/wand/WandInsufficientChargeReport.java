package dev.falseresync.wizcraft.common.report.wand;

import dev.falseresync.wizcraft.api.WizcraftApi;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class WandInsufficientChargeReport implements Report {
    public static final Identifier ID = wid("wand/insufficient_charge");
    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        WizcraftApi.getHud().getMessageDisplay().post(Text.translatable("hud.wizcraft.wand.insufficient_charge").styled(style -> style.withColor(Formatting.DARK_RED)));
    }
}
