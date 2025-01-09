package falseresync.wizcraft.common;

import eu.midnightdust.lib.config.MidnightConfig;
import falseresync.lib.registry.AutoRegistry;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.blockentity.WizcraftBlockEntities;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.entity.WizcraftEntities;
import falseresync.wizcraft.common.item.WizcraftItemGroups;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.recipe.WizcraftRecipeCustomIngredients;
import falseresync.wizcraft.common.recipe.WizcraftRecipeSerializers;
import falseresync.wizcraft.common.recipe.WizcraftRecipeTypes;
import falseresync.wizcraft.networking.WizcraftNetworking;
import falseresync.wizcraft.networking.WizcraftNetworkingServer;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MOD_ID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft");

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
        new AutoRegistry(MOD_ID, LOGGER)
                .link(Registries.BLOCK_ENTITY_TYPE, WizcraftBlockEntities.class)
                .link(Registries.ITEM_GROUP, WizcraftItemGroups.class)
                .link(Registries.DATA_COMPONENT_TYPE, WizcraftDataComponents.class)
                .link(Registries.ENTITY_TYPE, WizcraftEntities.class)
                .link(WizcraftReports.REGISTRY, WizcraftReports.class)
                .link(Registries.RECIPE_TYPE, WizcraftRecipeTypes.class)
                .link(Registries.RECIPE_SERIALIZER, WizcraftRecipeSerializers.class)
                .link(Registries.PARTICLE_TYPE, WizcraftParticleTypes.class);
        WizcraftRecipeCustomIngredients.init();
        WizcraftDataAttachments.init();
        WizcraftItemTags.init();
        WizcraftSounds.init();
        WizcraftNetworking.registerPackets();
        WizcraftNetworkingServer.registerReceivers();
    }
}
