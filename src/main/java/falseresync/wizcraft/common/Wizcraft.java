package falseresync.wizcraft.common;

import eu.midnightdust.lib.config.*;
import falseresync.lib.registry.*;
import falseresync.wizcraft.common.block.*;
import falseresync.wizcraft.common.blockentity.*;
import falseresync.wizcraft.common.data.attachment.*;
import falseresync.wizcraft.common.data.component.*;
import falseresync.wizcraft.common.entity.*;
import falseresync.wizcraft.common.item.*;
import falseresync.wizcraft.common.recipe.*;
import falseresync.wizcraft.networking.*;
import falseresync.wizcraft.networking.report.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

public class Wizcraft implements ModInitializer {
    public static final String MOD_ID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft");
    private static ChargeManager chargeManager;

    public static Identifier id(String id) {
        return Identifier.of(id);
    }

    public static Identifier wid(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static int findViewDistance(World world) {
        return world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().chunkLoadingManager.watchDistance;
    }

    public static long exchangeStackInSlotWithHand(PlayerEntity player, Hand hand, InventoryStorage storage, int slot, int maxAmount, @Nullable TransactionContext transaction) {
        var playerStack = player.getStackInHand(hand);
        var storedVariant = storage.getSlot(slot).getResource();

        if (storedVariant.isBlank() && !playerStack.isEmpty()) {
            return StorageUtil.move(PlayerInventoryStorage.of(player), storage, variant -> variant.matches(playerStack), maxAmount, transaction);
        }

        if (!storedVariant.isBlank() && playerStack.isEmpty()) {
            return StorageUtil.move(storage, PlayerInventoryStorage.of(player), variant -> variant.equals(storedVariant), maxAmount, transaction);
        }

        return 0;
    }

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, WizcraftConfig.class);
        WizcraftBlocks.init();
        WizcraftItems.init();
        ActivatorItem.registerBehaviors();
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

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!handler.player.hasAttached(WizcraftAttachments.INTRODUCED_TO_WIZCRAFT)) {
                handler.player.getInventory().offerOrDrop(new ItemStack(WizcraftItems.GRIMOIRE));
                handler.player.setAttached(WizcraftAttachments.INTRODUCED_TO_WIZCRAFT, true);
            }
        });
    }

    public static ChargeManager getChargeManager() {
        return chargeManager;
    }
}
