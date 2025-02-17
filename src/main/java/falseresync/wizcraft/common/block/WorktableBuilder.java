package falseresync.wizcraft.common.block;

import falseresync.lib.blockpattern.*;
import falseresync.wizcraft.common.blockentity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class WorktableBuilder<B extends WorktableBlockEntity> {
    protected Supplier<BlockEntityType<B>> type;
    @Nullable
    protected BlockEntityTicker<B> ticker;
    protected Supplier<BetterBlockPattern> pattern;
    protected BiPredicate<ServerWorld, ServerPlayerEntity> preconditions;

    public WorktableBuilder<B> type(Supplier<BlockEntityType<B>> type) {
        this.type = type;
        return this;
    }

    public WorktableBuilder<B> ticker(BlockEntityTicker<B> ticker) {
        this.ticker = ticker;
        return this;
    }

    public WorktableBuilder<B> pattern(Supplier<BetterBlockPattern> pattern) {
        this.pattern = pattern;
        return this;
    }

    public WorktableBuilder<B> preconditions(BiPredicate<ServerWorld, ServerPlayerEntity> preconditions) {
        this.preconditions = preconditions;
        return this;
    }

    public Function<AbstractBlock.Settings, WorktableBlock<B>> build() {
        Objects.requireNonNull(type, "BlockEntity type cannot be null");
        Objects.requireNonNull(pattern, "Pattern cannot be null");
        preconditions = preconditions == null ? (world, player) -> true : preconditions;

        return settings -> new WorktableBlock<>(settings) {
            private WorktableVariant<B> VARIANT;

            @Override
            public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
                return type.get().instantiate(pos, state);
            }

            @Override
            @Nullable
            public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
                return validateTicker(type, WorktableBuilder.this.type.get(), ticker);
            }

            @Override
            public WorktableVariant<B> getVariant() {
                if (VARIANT == null) {
                    VARIANT = new WorktableVariant<>(() -> this, pattern, preconditions);
                }
                return VARIANT;
            }
        };
    }
}
