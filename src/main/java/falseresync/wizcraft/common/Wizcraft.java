package falseresync.wizcraft.common;

import falseresync.lib.registry.AutoRegistry;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.blockentity.WizcraftBlockEntities;
import falseresync.wizcraft.common.config.WizcraftConfig;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.entity.WizcraftEntities;
import falseresync.wizcraft.common.item.ActivatorItem;
import falseresync.wizcraft.common.item.WizcraftItemGroups;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.item.focus.TransmutationFocusBehavior;
import falseresync.wizcraft.common.recipe.WizcraftCustomIngredients;
import falseresync.wizcraft.common.recipe.WizcraftRecipeSerializers;
import falseresync.wizcraft.common.recipe.WizcraftRecipes;
import falseresync.wizcraft.networking.WizcraftNetworking;
import falseresync.wizcraft.networking.WizcraftNetworkingServer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MOD_ID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft");
    private static ChargeManager chargeManager;
    private static WizcraftConfig config;

    public static Identifier id(String id) {
        return Identifier.of(id);
    }

    public static Identifier wid(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static ChargeManager getChargeManager() {
        return chargeManager;
    }

    public static WizcraftConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        config = AutoConfig.register(WizcraftConfig.class, JanksonConfigSerializer::new).getConfig();

        WizcraftBlocks.init();
        WizcraftItems.init();
        new AutoRegistry(MOD_ID, LOGGER)
                .link(Registries.BLOCK_ENTITY_TYPE, WizcraftBlockEntities.class)
                .link(Registries.ITEM_GROUP, WizcraftItemGroups.class)
                .link(Registries.DATA_COMPONENT_TYPE, WizcraftComponents.class)
                .link(Registries.ENTITY_TYPE, WizcraftEntities.class)
                .link(Registries.RECIPE_TYPE, WizcraftRecipes.class)
                .link(Registries.RECIPE_SERIALIZER, WizcraftRecipeSerializers.class)
                .link(Registries.PARTICLE_TYPE, WizcraftParticleTypes.class);
        WizcraftCustomIngredients.init();
        WizcraftAttachments.init();
        WizcraftItemTags.init();
        WizcraftSounds.init();
        WizcraftNetworking.registerPackets();
        WizcraftNetworkingServer.registerReceivers();

        chargeManager = new ChargeManager();

        ActivatorItem.registerBehaviors();
        TransmutationFocusBehavior.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!handler.player.hasAttached(WizcraftAttachments.INTRODUCED_TO_WIZCRAFT)) {
                handler.player.getInventory().offerOrDrop(new ItemStack(WizcraftItems.GRIMOIRE));
                handler.player.setAttached(WizcraftAttachments.INTRODUCED_TO_WIZCRAFT, true);
            }
        });
    }
}
