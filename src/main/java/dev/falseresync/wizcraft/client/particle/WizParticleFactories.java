package dev.falseresync.wizcraft.client.particle;

import dev.falseresync.wizcraft.common.particle.WizcraftParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

@Environment(EnvType.CLIENT)
public class WizParticleFactories {
    public static void register() {
        ParticleFactoryRegistry.getInstance().register(WizcraftParticleTypes.SPAGHETTIFICATION, SpaghettificationParticle.getFactory());
    }
}
