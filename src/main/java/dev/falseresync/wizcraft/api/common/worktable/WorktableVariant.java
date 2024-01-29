package dev.falseresync.wizcraft.api.common.worktable;

import dev.falseresync.wizcraft.api.common.blockpattern.BetterBlockPattern;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record WorktableVariant<T extends WorktableBlockEntity>(
        Supplier<WorktableBlock<T>> block,
        Supplier<BetterBlockPattern> pattern,
        BiPredicate<ServerWorld, ServerPlayerEntity> preconditions
) {
    private static final List<WorktableVariant<?>> UNBAKED_VARIANTS = new ArrayList<>();
    private static final List<BakedWorktableVariant<?>> BAKED_VARIANTS = new ArrayList<>();

    public static void register(WorktableVariant<?> variant) {
        UNBAKED_VARIANTS.add(variant);
    }

    public static List<BakedWorktableVariant<?>> getAll() {
        if (BAKED_VARIANTS.isEmpty()) {
            UNBAKED_VARIANTS.stream()
                    .map(variant -> new BakedWorktableVariant<>(variant.block.get(), variant.pattern.get(), variant.preconditions()))
                    .sorted(Comparator.comparingInt(variantA -> variantA.pattern.getSize()))
                    .collect(Collectors.toCollection(() -> BAKED_VARIANTS));
        }
        return BAKED_VARIANTS;
    }

    public static List<BakedWorktableVariant<?>> getForPlayer(ServerWorld world, ServerPlayerEntity player) {
        return getAll().stream().filter(variant -> variant.preconditions.test(world, player)).toList();
    }

    public record BakedWorktableVariant<T extends WorktableBlockEntity>(
            WorktableBlock<T> block,
            BetterBlockPattern pattern,
            BiPredicate<ServerWorld, ServerPlayerEntity> preconditions
    ) {
    }
}
