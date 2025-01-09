package falseresync.wizcraft.common;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;

public class WizcraftParticleTypes {
    public static final @RegistryObject ParticleType<ItemStackParticleEffect> SPAGHETTIFICATION =
            FabricParticleTypes.complex(ItemStackParticleEffect::createCodec, ItemStackParticleEffect::createPacketCodec);
    public static final @RegistryObject SimpleParticleType CHARGING = FabricParticleTypes.simple();
}
