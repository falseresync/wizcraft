package dev.falseresync.wizcraft.api.common.blockpattern;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldView;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class BetterBlockPattern {
    protected final Predicate<CachedBlockPosition>[][][] pattern;
    protected final int width;
    protected final int height;
    protected final int depth;
    protected final int biggestCoordinate;
    protected final int size;
    protected final Direction[] possibleUp;
    protected final Direction[] possibleForwards;

    public BetterBlockPattern(Predicate<CachedBlockPosition>[][][] pattern, boolean preserveUp) {
        this.pattern = pattern;
        width = pattern.length;
        height = pattern[0].length;
        depth = pattern[0][0].length;
        biggestCoordinate = Math.max(Math.max(width, depth), height);
        size = width * height * depth;
        possibleUp = preserveUp ? new Direction[]{Direction.UP} : Direction.values();
        possibleForwards = preserveUp
                ? new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}
                : Direction.values();
    }

    public int getSize() {
        return size;
    }

    public Match searchAround(WorldView world, BlockPos startingPos) {
        var results = new Int2ObjectRBTreeMap<Match>(Integer::compareTo);
        LoadingCache<BlockPos, CachedBlockPosition> cache = Caffeine.newBuilder()
                .build(pos -> new CachedBlockPosition(world, pos, false));
        for (var pos : BlockPos.iterate(startingPos, startingPos.add(biggestCoordinate - 1, biggestCoordinate - 1, biggestCoordinate - 1))) {
            for (var forwards : possibleForwards) {
                for (var up : possibleUp) {
                    if (up != forwards && up != forwards.getOpposite()) {
                        var result = test(pos, forwards, up, cache);
                        if (result.isCompleted()) {
                            return result;
                        }
                        results.put(result.delta.size(), result);
                    }
                }
            }
        }

        return Objects.requireNonNull(results.firstEntry()).getValue();
    }

    public Match test(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache) {
        var delta = ImmutableList.<CachedBlockPosition>builder();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    var mew = changeBasis(frontTopLeft, forwards, up, x, y, z);
                    var cachedPos = cache.get(mew);
                    if (!pattern[x][y][z].test(cachedPos)) {
                        delta.add(cachedPos);
                    }
                }
            }
        }

        return new Match(delta.build(), frontTopLeft, forwards, up, width, height, depth, size);
    }

    /**
     * @param origin Front-Top-Left block of the pattern
     */
    // https://stackoverflow.com/q/19621069
    // https://youtu.be/P2LTAUO1TdA
    protected static BlockPos changeBasis(BlockPos origin, Direction forwards, Direction up, int x, int y, int z) {
        Preconditions.checkArgument(up != forwards && up != forwards.getOpposite(), "Invalid combination of forwards and up: %s and %s", forwards, up);
        var localOZ = forwards.getVector();
        var localOY = up.getVector();
        var localOX = localOY.crossProduct(localOZ); // A vector perpendicular to the XZ plane
        return origin.subtract(new Vec3i(
                localOX.getX() * x + localOY.getX() * y + localOZ.getX() * z,
                localOX.getY() * x + localOY.getY() * y + localOZ.getY() * z,
                localOX.getZ() * x + localOY.getZ() * y + localOZ.getZ() * z));
    }

    public record Match(List<CachedBlockPosition> delta,
                        List<BlockPos> deltaAsBlockPos,
                        BlockPos frontTopLeft, Direction forwards, Direction up,
                        int width, int height, int depth, int size) {
        public Match(List<CachedBlockPosition> delta,
                     BlockPos frontTopLeft, Direction forwards, Direction up,
                     int width, int height, int depth, int size) {
            this(delta, delta.stream().map(CachedBlockPosition::getBlockPos).toList(), frontTopLeft, forwards, up, width, height, depth, size);
        }

        public boolean isCompleted() {
            return delta.isEmpty();
        }

        public boolean isHalfwayCompleted() {
            return (double) Math.abs(delta.size() - size) / size > 0.5;
        }
    }
}
