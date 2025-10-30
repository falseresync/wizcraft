package falseresync.wizcraft.common.block;

import falseresync.lib.blockpattern.BetterBlockPattern;
import falseresync.wizcraft.common.blockentity.WorktableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class WorktableBuilder<B extends WorktableBlockEntity> {
    protected Supplier<BlockEntityType<B>> type;
    @Nullable
    protected BlockEntityTicker<B> ticker;
    protected Supplier<BetterBlockPattern> pattern;
    protected BiPredicate<ServerLevel, ServerPlayer> preconditions;

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

    public WorktableBuilder<B> preconditions(BiPredicate<ServerLevel, ServerPlayer> preconditions) {
        this.preconditions = preconditions;
        return this;
    }

    public Function<BlockBehaviour.Properties, WorktableBlock<B>> build() {
        Objects.requireNonNull(type, "BlockEntity type cannot be null");
        Objects.requireNonNull(pattern, "Pattern cannot be null");
        preconditions = preconditions == null ? (world, player) -> true : preconditions;

        return settings -> new WorktableBlock<>(settings) {
            private WorktableVariant<B> VARIANT;

            @Override
            public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                return type.get().create(pos, state);
            }

            @Override
            @Nullable
            public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
                return createTickerHelper(type, WorktableBuilder.this.type.get(), ticker);
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
