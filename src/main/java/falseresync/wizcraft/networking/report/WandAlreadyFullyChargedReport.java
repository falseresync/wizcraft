package falseresync.wizcraft.networking.report;

import falseresync.wizcraft.client.WizcraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class WandAlreadyFullyChargedReport implements Report {

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
        WizcraftClient.hud.getMessageDisplay().post(Text.translatable("hud.wizcraft.wand.already_charged"));
    }
}
