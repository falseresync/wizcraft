package falseresync.wizcraft.networking.report;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;

public class CometWarpNoAnchorReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
//        WizcraftApi.getHud().getMessageDisplay().post(Text.translatable("hud.wizcraft.wand.no_anchor"));
    }
}
