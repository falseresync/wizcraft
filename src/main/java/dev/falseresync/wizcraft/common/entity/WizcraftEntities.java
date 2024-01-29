package dev.falseresync.wizcraft.common.entity;

import dev.falseresync.wizcraft.common.Wizcraft;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizcraftEntities {
    public static final EntityType<StarProjectileEntity> STAR_PROJECTILE;
    private static final Map<Identifier, EntityType<?>> TO_REGISTER = new HashMap<>();

    static {
        STAR_PROJECTILE = r("star_projectile", FabricEntityTypeBuilder.<StarProjectileEntity>create()
                .entityFactory(StarProjectileEntity::new)
                .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                .fireImmune()
                .disableSaving()
                .trackRangeBlocks(16)
                .build());
    }

    private static <T extends Entity> EntityType<T> r(String id, EntityType<T> entityType) {
        TO_REGISTER.put(new Identifier(Wizcraft.MODID, id), entityType);
        return entityType;
    }

    public static void register(BiConsumer<Identifier, EntityType<?>> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
