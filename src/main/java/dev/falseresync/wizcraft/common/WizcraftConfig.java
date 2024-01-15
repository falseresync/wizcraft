package dev.falseresync.wizcraft.common;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public final class WizcraftConfig {
    private static final String GENERAL = "general";
    private static final String CHEATS = "cheats";

    public static final ConfigClassHandler<WizcraftConfig> HANDLER = ConfigClassHandler.createBuilder(WizcraftConfig.class)
            .id(new Identifier(Wizcraft.MODID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("wizcraft.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    @AutoGen(category = CHEATS)
    @TickBox
    public boolean expendWandChargeInSurvival = true;
}

