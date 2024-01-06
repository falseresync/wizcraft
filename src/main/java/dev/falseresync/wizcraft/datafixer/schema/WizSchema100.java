package dev.falseresync.wizcraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import java.util.Map;
import java.util.function.Supplier;

public class WizSchema100 extends WizSchema {
    public WizSchema100(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        var entities = super.registerEntities(schema);
        schema.registerSimple(entities, wizcraft("star_projectile"));
        return entities;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        var blockEntities = super.registerBlockEntities(schema);
        withItems(schema, blockEntities, wizcraft("lensing_pedestal"));
        withItems(schema, blockEntities, wizcraft("energized_worktable"));
        return blockEntities;
    }
}
