package dev.falseresync.wizcraft.common.report.lensedworktable;

import dev.falseresync.wizcraft.api.common.report.AreaReport;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.report.ReportUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LensedWorktableSuccessAreaReport implements AreaReport {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "lensed_worktable_success");
    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void executeOnArea(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
        ReportUtil.addFireworkSparkles(world, pos.toCenterPos().add(0, 0.75, 0));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void execute(ClientPlayerEntity player) {

    }
}
