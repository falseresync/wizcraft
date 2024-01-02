package dev.falseresync.wizcraft.common;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WizUtils {
    public static int findViewDistance(World world) {
        return world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.watchDistance;
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
}
