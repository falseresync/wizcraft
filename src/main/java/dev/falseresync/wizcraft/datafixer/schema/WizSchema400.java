package dev.falseresync.wizcraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import java.util.Map;
import java.util.function.Supplier;

public class WizSchema400 extends WizSchema{
    public WizSchema400(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        var blockEntities = super.registerBlockEntities(schema);
        blockEntities.put(wizcraft("crafting_worktable"), blockEntities.remove(wizcraft("worktable")));
        withItems(schema, blockEntities, wizcraft("charging_worktable"));
        return blockEntities;
    }
}
