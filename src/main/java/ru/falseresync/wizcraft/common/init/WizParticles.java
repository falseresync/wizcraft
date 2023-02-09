package ru.falseresync.wizcraft.common.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import ru.falseresync.wizcraft.lib.autoregistry.RegistryObject;

public class WizParticles {
    public static final @RegistryObject DefaultParticleType CONCOCTION_BUBBLES = FabricParticleTypes.simple();
}
