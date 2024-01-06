package dev.falseresync.wizcraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import java.util.Map;
import java.util.function.Supplier;

public class WizSchema200 extends WizSchema{
    public WizSchema200(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        var blockEntities = super.registerBlockEntities(schema);
        blockEntities.put(wizcraft("plated_worktable"), blockEntities.remove(wizcraft("energized_worktable")));
        return blockEntities;
    }
}
