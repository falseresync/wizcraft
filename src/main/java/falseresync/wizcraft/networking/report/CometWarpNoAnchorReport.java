package falseresync.wizcraft.networking.report;

import falseresync.wizcraft.client.WizcraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class CometWarpNoAnchorReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        WizcraftClient.getHud().getMessageDisplay().post(Text.translatable("hud.wizcraft.wand.no_anchor"));
    }
}
