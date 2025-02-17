package falseresync.wizcraft.client.particle;

import falseresync.wizcraft.common.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.particle.v1.*;

@Environment(EnvType.CLIENT)
public class WizcraftParticleFactories {
    public static void init() {
        ParticleFactoryRegistry.getInstance().register(WizcraftParticleTypes.SPAGHETTIFICATION, SpaghettificationParticle.getFactory());
        ParticleFactoryRegistry.getInstance().register(WizcraftParticleTypes.CHARGING, ChargingParticleFactory::new);
    }
}
