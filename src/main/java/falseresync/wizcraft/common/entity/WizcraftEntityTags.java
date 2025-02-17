package falseresync.wizcraft.common.entity;

import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class WizcraftEntityTags {
    public static final TagKey<EntityType<?>> PASSES_THROUGH_ENERGY_VEIL = TagKey.of(RegistryKeys.ENTITY_TYPE, wid("passes_through_energy_veil"));
}
