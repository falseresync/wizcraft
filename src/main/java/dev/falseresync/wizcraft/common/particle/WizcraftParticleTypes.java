package dev.falseresync.wizcraft.common.particle;

import dev.falseresync.wizcraft.common.Wizcraft;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizcraftParticleTypes {
    private static final Map<Identifier, ParticleType<?>> TO_REGISTER = new HashMap<>();

    public static final ParticleType<ItemStackParticleEffect> SPAGHETTIFICATION =
            r("spaghettification", FabricParticleTypes.complex(ItemStackParticleEffect.PARAMETERS_FACTORY));

    private static <T extends ParticleEffect> ParticleType<T> r(String id, ParticleType<T> particleType) {
        TO_REGISTER.put(new Identifier(Wizcraft.MODID, id), particleType);
        return particleType;
    }

    public static void register(BiConsumer<Identifier, ParticleType<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
