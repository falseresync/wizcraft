package falseresync.wizcraft.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftEntityTags {
    public static final TagKey<EntityType<?>> PASSES_THROUGH_ENERGY_VEIL = TagKey.create(Registries.ENTITY_TYPE, wid("passes_through_energy_veil"));

    public static final TagKey<EntityType<?>> TRANSMUTATION_AGEABLE = TagKey.create(Registries.ENTITY_TYPE, wid("transmutation/ageable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_TRANSFORMABLE = TagKey.create(Registries.ENTITY_TYPE, wid("transmutation/transformable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_RESULT = TagKey.create(Registries.ENTITY_TYPE, wid("transmutation/results"));
}
