package falseresync.wizcraft.networking.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;

public class WorktableIncompleteReport implements Report {
    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
//        WizcraftApi.getHud().getMessageDisplay().post(Text.translatable("hud.wizcraft.worktable.incomplete_worktable"));
    }
}
