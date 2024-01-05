package dev.falseresync.wizcraft.common.report.skywand;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetInstancePriority;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class SuccessfullyChargedReport implements Report {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "successfully_charged");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void execute(ClientPlayerEntity player) {
        var world = player.getWorld();
        var rotation = player.getRotationVec(1);
        var orthogonalDistance = 1;
        var pos = player.getEyePos()
                .add(player.getHandPosOffset(WizItems.SKY_WAND))
                .add(rotation.x * orthogonalDistance, -0.25, rotation.z * orthogonalDistance);
        var random = world.getRandom();
        for (int i = 0; i < random.nextBetween(5, 10); i++) {
            world.addParticle(
                    ParticleTypes.FIREWORK,
                    pos.x,
                    pos.y,
                    pos.z,
                    (random.nextFloat() - 0.5) / 2,
                    random.nextFloat() / 2,
                    (random.nextFloat() - 0.5) / 2);
        }
        player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, 1F, 1.25F);
        WizHud.STATUS_MESSAGE.override(
                Text.translatable("hud.wizcraft.sky_wand.successfully_charged").styled(style -> style.withColor(Formatting.GOLD)),
                WidgetInstancePriority.HIGH);
    }
}
