package falseresync.wizcraft.common;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class WizcraftUtil {
    private static final Function<Level, Integer> memo$findViewDistance = Util.memoize((Level world) -> world.isClientSide()
            ? Minecraft.getInstance().options.getEffectiveRenderDistance()
            : ((ServerLevel) world).getChunkSource().chunkMap.serverViewDistance);

    public static <T> Optional<T> nextRandomEntry(ServerLevel world, TagKey<T> tag, RandomSource random) {
        return world.registryAccess()
                .registry(tag.registry())
                .map(registry -> registry.getOrCreateTag(tag))
                .flatMap(entries -> entries.getRandomElement(random).map(Holder::value));
    }

    /**
     * @return memoized(!) view distance
     */
    public static int findViewDistance(Level world) {
        return memo$findViewDistance.apply(world);
    }

    public static long exchangeStackInSlotWithHand(Player player, InteractionHand hand, InventoryStorage storage, int slot, int maxAmount, @Nullable TransactionContext transaction) {
        var playerStack = player.getItemInHand(hand);
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
