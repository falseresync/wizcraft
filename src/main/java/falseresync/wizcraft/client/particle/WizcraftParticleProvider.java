package falseresync.wizcraft.client.particle;

import falseresync.wizcraft.common.WizcraftParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

@Environment(EnvType.CLIENT)
public class WizcraftParticleProvider {
    public static void init() {
        ParticleFactoryRegistry.getInstance().register(WizcraftParticleTypes.SPAGHETTIFICATION, SpaghettificationParticle.makeProvider());
        ParticleFactoryRegistry.getInstance().register(WizcraftParticleTypes.CHARGING, ChargingParticleProvider::new);
    }
}
