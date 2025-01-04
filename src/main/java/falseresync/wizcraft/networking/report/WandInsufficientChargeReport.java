package falseresync.wizcraft.networking.report;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;

public class WandInsufficientChargeReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
//        WizcraftApi.getHud().getMessageDisplay().post(Text.translatable("hud.wizcraft.wand.insufficient_charge").styled(style -> style.withColor(Formatting.DARK_RED)));
    }
}
