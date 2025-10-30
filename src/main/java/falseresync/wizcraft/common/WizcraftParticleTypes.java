package falseresync.wizcraft.common;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

public class WizcraftParticleTypes {
    public static final @RegistryObject ParticleType<ItemParticleOption> SPAGHETTIFICATION =
            FabricParticleTypes.complex(ItemParticleOption::codec, ItemParticleOption::streamCodec);
    public static final @RegistryObject SimpleParticleType CHARGING = FabricParticleTypes.simple();
}
