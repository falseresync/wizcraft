package falseresync.wizcraft.common;

import falseresync.lib.registry.*;
import falseresync.wizcraft.common.block.*;
import falseresync.wizcraft.common.blockentity.*;
import falseresync.wizcraft.common.config.*;
import falseresync.wizcraft.common.data.attachment.*;
import falseresync.wizcraft.common.data.component.*;
import falseresync.wizcraft.common.entity.*;
import falseresync.wizcraft.common.item.*;
import falseresync.wizcraft.common.item.focus.*;
import falseresync.wizcraft.common.recipe.*;
import falseresync.wizcraft.networking.*;
import falseresync.wizcraft.networking.report.*;
import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.serializer.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import org.slf4j.*;

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
                .link(WizcraftReports.REGISTRY, WizcraftReports.class)
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
