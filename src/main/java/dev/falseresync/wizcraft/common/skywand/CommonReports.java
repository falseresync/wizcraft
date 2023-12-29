package dev.falseresync.wizcraft.common.skywand;

import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class CommonReports {
    public static void insufficientCharge(World world, LivingEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
            WizHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.sky_wand.insufficient_charge")
                    .styled(style -> style.withColor(Formatting.DARK_RED)));
        }
    }
}
