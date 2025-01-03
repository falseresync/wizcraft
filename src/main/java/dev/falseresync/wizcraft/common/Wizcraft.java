package dev.falseresync.wizcraft.common;

import dev.falseresync.wizcraft.api.WizcraftRegistries;
import dev.falseresync.wizcraft.common.block.WizcraftBlocks;
import dev.falseresync.wizcraft.common.block.entity.WizcraftBlockEntities;
import dev.falseresync.wizcraft.common.entity.WizcraftEntities;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.common.particle.WizcraftParticleTypes;
import dev.falseresync.wizcraft.common.recipe.WizcraftRecipeSerializers;
import dev.falseresync.wizcraft.common.recipe.WizcraftRecipes;
import dev.falseresync.wizcraft.common.report.WizcraftReports;
import dev.falseresync.wizcraft.common.wand.focus.WizcraftFocuses;
import dev.falseresync.wizcraft.network.WizcraftServerNetworking;
import dev.falseresync.wizcraft.network.WizcraftNetworking;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MOD_ID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft");

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, WizcraftConfig.class);
        WizcraftRegistries.register();
        WizcraftReports.register((id, report) -> Registry.register(WizcraftRegistries.REPORTS, id, report));
        WizcraftBlocks.register((id, block) -> Registry.register(Registries.BLOCK, id, block));
        WizcraftBlockEntities.register((id, type) -> Registry.register(Registries.BLOCK_ENTITY_TYPE, id, type));
        WizcraftItems.registerItems((id, item) -> Registry.register(Registries.ITEM, id, item));
        WizcraftItems.registerItemGroups((id, item) -> Registry.register(Registries.ITEM_GROUP, id, item));
        WizcraftEntities.register((id, type) -> Registry.register(Registries.ENTITY_TYPE, id, type));
        WizcraftFocuses.register((id, focus) -> Registry.register(WizcraftRegistries.FOCUS_TYPE, id, focus));
        WizcraftRecipeSerializers.register((id, serializer) -> Registry.register(Registries.RECIPE_SERIALIZER, id, serializer));
        WizcraftRecipes.register((id, type) -> Registry.register(Registries.RECIPE_TYPE, id, type));
        WizcraftParticleTypes.register((id, type) -> Registry.register(Registries.PARTICLE_TYPE, id, type));
        WizcraftNetworking.registerPackets();
        WizcraftServerNetworking.registerReceivers();
    }

    public static Identifier id(String id) {
        return Identifier.of(id);
    }

    public static Identifier wid(String path) {
        return Identifier.of(MOD_ID, path);
    }
}