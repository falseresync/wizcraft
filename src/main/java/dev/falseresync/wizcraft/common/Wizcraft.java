package dev.falseresync.wizcraft.common;

import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.entity.WizEntities;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.recipe.WizRecipeSerializers;
import dev.falseresync.wizcraft.common.recipe.WizRecipes;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import dev.falseresync.wizcraft.network.server.WizServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MODID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("wizcraft");

    @Override
    public void onInitialize() {
        WizRegistries.register();
        WizBlocks.register((id, block) -> Registry.register(Registries.BLOCK, id, block));
        WizBlockEntities.register((id, type) -> Registry.register(Registries.BLOCK_ENTITY_TYPE, id, type));
        WizItems.registerItems((id, item) -> Registry.register(Registries.ITEM, id, item));
        WizItems.registerItemGroups((id, item) -> Registry.register(Registries.ITEM_GROUP, id, item));
        WizEntities.register((id, type) -> Registry.register(Registries.ENTITY_TYPE, id, type));
        WizFocuses.register((id, focus) -> Registry.register(WizRegistries.FOCUSES, id, focus));
        WizRecipeSerializers.register((id, serializer) -> Registry.register(Registries.RECIPE_SERIALIZER, id, serializer));
        WizRecipes.register((id, type) -> Registry.register(Registries.RECIPE_TYPE, id, type));
        WizServerNetworking.registerReceivers();
    }
}