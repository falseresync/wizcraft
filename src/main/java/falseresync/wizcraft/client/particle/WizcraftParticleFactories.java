package falseresync.wizcraft.client.particle;

import falseresync.wizcraft.common.particle.WizcraftParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

@Environment(EnvType.CLIENT)
public class WizcraftParticleFactories {
    public static void init() {
        ParticleFactoryRegistry.getInstance().register(WizcraftParticleTypes.SPAGHETTIFICATION, SpaghettificationParticle.getFactory());
    }
}
