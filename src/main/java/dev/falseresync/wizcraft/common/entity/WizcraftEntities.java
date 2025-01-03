package dev.falseresync.wizcraft.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftEntities {
    public static final EntityType<StarProjectileEntity> STAR_PROJECTILE;
    private static final Map<Identifier, EntityType<?>> TO_REGISTER = new HashMap<>();

    static {
        STAR_PROJECTILE = r("star_projectile", EntityType.Builder
                .<StarProjectileEntity>create(StarProjectileEntity::new, SpawnGroup.MISC)
                .dimensions(0.5F, 0.5F)
                .makeFireImmune()
                .disableSaving()
                .maxTrackingRange(16)
                .build());
    }

    private static <T extends Entity> EntityType<T> r(String id, EntityType<T> entityType) {
        TO_REGISTER.put(wid(id), entityType);
        return entityType;
    }

    public static void register(BiConsumer<Identifier, EntityType<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
