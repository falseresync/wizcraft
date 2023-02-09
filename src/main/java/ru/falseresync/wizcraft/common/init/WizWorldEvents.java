package ru.falseresync.wizcraft.common.init;

import com.google.common.primitives.Doubles;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import ru.falseresync.wizcraft.lib.worldevents.WorldEventUtil;

public class WizWorldEvents {
    public static final int MAGIC_CAULDRON_DISSOLVE = WorldEventUtil.registerLocal((client, world, renderer, pos, data) -> {
        int i = world.random.nextBetween(7, 15) / (client.options.getParticles().getValue() == ParticlesMode.DECREASED ? 2 : 1);

        for (int j = 0; j < i; j++) {
            world.addParticle(
                    WizParticles.CONCOCTION_BUBBLES,
                    pos.getX() + 0.5 - Doubles.constrainToRange(world.random.nextGaussian(), -1, 1) / 3,
                    pos.getY() + 1,
                    pos.getZ() + 0.5 - Doubles.constrainToRange(world.random.nextGaussian(), -1, 1) / 3,
                    0, 0, 0);
        }

        world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
    });

    public static void init() {
    }
}
