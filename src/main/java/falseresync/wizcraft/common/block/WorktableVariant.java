package falseresync.wizcraft.common.block;

import falseresync.lib.blockpattern.BetterBlockPattern;
import falseresync.wizcraft.common.blockentity.WorktableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record WorktableVariant<T extends WorktableBlockEntity>(
        Supplier<WorktableBlock<T>> block,
        Supplier<BetterBlockPattern> pattern,
        BiPredicate<ServerLevel, ServerPlayer> preconditions
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

    public static List<Baked<?>> getForPlayer(ServerLevel world, ServerPlayer player) {
        return getAll().stream().filter(variant -> variant.preconditions.test(world, player)).toList();
    }

    public static List<Matched<?>> getForPlayerAndSearchAround(ServerLevel world, ServerPlayer player, BlockPos startingPos) {
        return getForPlayer(world, player)
                .stream()
                .map(baked -> new Matched<>(baked.block, baked.pattern.searchAround(world, startingPos)))
                .collect(Collectors.toList());
    }

    public record Baked<T extends WorktableBlockEntity>(
            WorktableBlock<T> block,
            BetterBlockPattern pattern,
            BiPredicate<ServerLevel, ServerPlayer> preconditions
    ) {}

    public record Matched<T extends WorktableBlockEntity>(
            WorktableBlock<T> block,
            BetterBlockPattern.Match match
    ) {}
}