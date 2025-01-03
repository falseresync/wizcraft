package dev.falseresync.wizcraft.common.report.worktable;

import dev.falseresync.wizcraft.api.WizcraftApi;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class WorktableIncompleteReport implements Report {
    public static final Identifier ID = wid("worktable/incomplete");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        WizcraftApi.getHud().getMessageDisplay().post(Text.translatable("hud.wizcraft.worktable.incomplete_worktable"));
    }
}
