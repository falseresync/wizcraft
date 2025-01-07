package falseresync.wizcraft.networking.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class WorktableIncompleteReport implements Report {
    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.translatable("hud.wizcraft.worktable.incomplete_worktable"), false);
    }
}
