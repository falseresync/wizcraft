package falseresync.wizcraft.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftEntityTags {
    public static final TagKey<EntityType<?>> PASSES_THROUGH_ENERGY_VEIL = TagKey.of(RegistryKeys.ENTITY_TYPE, wid("passes_through_energy_veil"));
}
