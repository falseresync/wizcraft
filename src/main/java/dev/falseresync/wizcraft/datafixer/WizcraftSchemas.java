package dev.falseresync.wizcraft.datafixer;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import dev.falseresync.wizcraft.datafixer.fix.SkyWandDataKeysRenameFix;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

public class WizcraftSchemas implements LevelComponentInitializer {
    public static final int CURRENT_SCHEMA_VERSION = 100;
    public static final ComponentKey<SchemaVersionComponent> SCHEMA_VERSION_COMPONENT =
            ComponentRegistry.getOrCreate(new Identifier("wizcraft", "schema_version"), SchemaVersionComponent.class);
    private static final BiFunction<Integer, Schema, Schema> EMPTY = IdentifierNormalizingSchema::new;
    public static final DataFixer FIXER = create();

    private static DataFixer create() {
        var builder = new DataFixerBuilder(SharedConstants.getGameVersion().getSaveVersion().getId());

        var schema100 = builder.addSchema(100, EMPTY);
        builder.addFixer(new SkyWandDataKeysRenameFix(schema100));

        return builder.buildUnoptimized();
    }

    @Override
    public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
        registry.register(SCHEMA_VERSION_COMPONENT, it -> new SchemaVersionComponent());
    }
}
