package dev.falseresync.wizcraft.common;

import dev.falseresync.wizcraft.api.WizRegistries;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.entity.WizEntities;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.particle.WizParticleTypes;
import dev.falseresync.wizcraft.common.recipe.WizRecipeSerializers;
import dev.falseresync.wizcraft.common.recipe.WizRecipes;
import dev.falseresync.wizcraft.common.report.WizReports;
import dev.falseresync.wizcraft.common.wand.focus.WizFocuses;
import dev.falseresync.wizcraft.network.WizServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MODID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft");

    @Override
    public void onInitialize() {
        WizRegistries.register();
        WizReports.register((id, report) -> Registry.register(WizRegistries.REPORTS, id, report));
        WizBlocks.register((id, block) -> Registry.register(Registries.BLOCK, id, block));
        WizBlockEntities.register((id, type) -> Registry.register(Registries.BLOCK_ENTITY_TYPE, id, type));
        WizItems.registerItems((id, item) -> Registry.register(Registries.ITEM, id, item));
        WizItems.registerItemGroups((id, item) -> Registry.register(Registries.ITEM_GROUP, id, item));
        WizEntities.register((id, type) -> Registry.register(Registries.ENTITY_TYPE, id, type));
        WizFocuses.register((id, focus) -> Registry.register(WizRegistries.FOCUS_TYPE, id, focus));
        WizRecipeSerializers.register((id, serializer) -> Registry.register(Registries.RECIPE_SERIALIZER, id, serializer));
        WizRecipes.register((id, type) -> Registry.register(Registries.RECIPE_TYPE, id, type));
        WizParticleTypes.register((id, type) -> Registry.register(Registries.PARTICLE_TYPE, id, type));
        WizServerNetworking.registerReceivers();
    }
}