package falseresync.wizcraft.common.particle;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleType;

public class WizcraftParticleTypes {
    public static final @RegistryObject ParticleType<ItemStackParticleEffect> SPAGHETTIFICATION =
            FabricParticleTypes.complex(ItemStackParticleEffect::createCodec, ItemStackParticleEffect::createPacketCodec);
}
