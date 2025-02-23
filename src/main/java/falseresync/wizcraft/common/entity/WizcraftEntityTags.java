package falseresync.wizcraft.common.entity;

import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class WizcraftEntityTags {
    public static final TagKey<EntityType<?>> PASSES_THROUGH_ENERGY_VEIL = TagKey.of(RegistryKeys.ENTITY_TYPE, wid("passes_through_energy_veil"));

    public static final TagKey<EntityType<?>> TRANSMUTATION_AGEABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, wid("transmutation/ageable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_TRANSFORMABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, wid("transmutation/transformable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_RESULT = TagKey.of(RegistryKeys.ENTITY_TYPE, wid("transmutation/results"));
}
