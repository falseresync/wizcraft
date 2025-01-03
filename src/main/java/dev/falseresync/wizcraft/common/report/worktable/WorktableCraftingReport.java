package dev.falseresync.wizcraft.common.report.worktable;

import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class WorktableCraftingReport implements MultiplayerReport {
    public static final Identifier ID = wid("worktable/crafting");
    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        world.playSound(null, pos, SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value(), SoundCategory.BLOCKS, 1f, 1f);
    }
}
