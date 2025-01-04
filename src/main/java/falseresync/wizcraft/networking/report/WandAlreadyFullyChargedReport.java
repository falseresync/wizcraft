package falseresync.wizcraft.networking.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;

public class WandAlreadyFullyChargedReport implements Report {

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT);
//        WizcraftApi.getHud().getMessageDisplay().post(Text.translatable("hud.wizcraft.wand.already_charged"));
    }
}
