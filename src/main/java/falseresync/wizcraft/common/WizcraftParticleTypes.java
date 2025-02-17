package falseresync.wizcraft.common;

import falseresync.lib.registry.*;
import net.fabricmc.fabric.api.particle.v1.*;
import net.minecraft.particle.*;

public class WizcraftParticleTypes {
    public static final @RegistryObject ParticleType<ItemStackParticleEffect> SPAGHETTIFICATION =
            FabricParticleTypes.complex(ItemStackParticleEffect::createCodec, ItemStackParticleEffect::createPacketCodec);
    public static final @RegistryObject SimpleParticleType CHARGING = FabricParticleTypes.simple();
}
