package dev.falseresync.wizcraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

import java.util.Map;
import java.util.function.Supplier;

public abstract class WizSchema extends IdentifierNormalizingSchema {
    public WizSchema(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static String wizcraft(String id) {
        return "wizcraft:" + id;
    }

    protected static void withItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityId) {
        schema.register(map, blockEntityId, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }
}
