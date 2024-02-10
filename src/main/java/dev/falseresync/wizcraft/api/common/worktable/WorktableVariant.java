package dev.falseresync.wizcraft.api.common.worktable;

import dev.falseresync.wizcraft.api.common.blockpattern.BetterBlockPattern;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

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
    private static final List<Baked<?>> BAKED_VARIANTS = new ArrayList<>();

    public static void register(WorktableVariant<?> variant) {
        UNBAKED_VARIANTS.add(variant);
    }

    public static List<Baked<?>> getAll() {
        if (BAKED_VARIANTS.isEmpty()) {
            UNBAKED_VARIANTS.stream()
                    .map(variant -> new Baked<>(variant.block.get(), variant.pattern.get(), variant.preconditions()))
                    .sorted(Comparator.comparingInt(variantA -> variantA.pattern.getSize()))
                    .collect(Collectors.toCollection(() -> BAKED_VARIANTS));
        }
        return BAKED_VARIANTS;
    }

    public static List<Baked<?>> getForPlayer(ServerWorld world, ServerPlayerEntity player) {
        return getAll().stream().filter(variant -> variant.preconditions.test(world, player)).toList();
    }

    public static List<Matched<?>> getForPlayerAndSearchAround(ServerWorld world, ServerPlayerEntity player, BlockPos startingPos) {
        return getForPlayer(world, player)
                .stream()
                .map(baked -> new Matched<>(baked.block, baked.pattern.searchAround(world, startingPos)))
                .collect(Collectors.toList());
    }

    public record Baked<T extends WorktableBlockEntity>(
            WorktableBlock<T> block,
            BetterBlockPattern pattern,
            BiPredicate<ServerWorld, ServerPlayerEntity> preconditions
    ) {}

    public record Matched<T extends WorktableBlockEntity>(
            WorktableBlock<T> block,
            BetterBlockPattern.Match match
    ) {}
}
