package falseresync.lib.blockpattern;

import com.google.common.base.*;
import net.minecraft.block.pattern.*;

import java.util.*;
import java.util.function.Predicate;

public class BetterBlockPatternBuilder {
    protected final List<String[]> layers = new ArrayList<>();
    protected final Map<Character, Predicate<CachedBlockPosition>> keys = new HashMap<>();
    protected int width = 0;
    protected int depth = 0;
    protected boolean preserveUp = false;
    protected boolean sidewaysLayers = false;

    public BetterBlockPatternBuilder() {
        keys.put(' ', pos -> true);
    }

    public BetterBlockPatternBuilder layer(String... pattern) {
        Preconditions.checkArgument(pattern.length > 0 && !pattern[0].isEmpty(), "Empty patterns are disallowed");

        if (depth == 0) {
            depth = pattern.length;
        }
        Preconditions.checkArgument(pattern.length == depth, "All layers must have equal depths, but found different: %d and %d", pattern.length, depth);

        if (width == 0) {
            width = pattern[0].length();
        }
        for (var subLayer : pattern) {
            Preconditions.checkArgument(subLayer.length() == width, "All sub-layers must have equal widths, but found different: %d and %d", subLayer.length(), width);

            for (var key : subLayer.toCharArray()) {
                keys.putIfAbsent(key, null);
            }
        }

        layers.add(pattern);
        return this;
    }

    public BetterBlockPatternBuilder where(char key, Predicate<CachedBlockPosition> value) {
        keys.put(key, value);
        return this;
    }

    public BetterBlockPatternBuilder preserveUp() {
        this.preserveUp = true;
        return this;
    }

    public BetterBlockPatternBuilder sidewaysLayers() {
        this.sidewaysLayers = true;
        return this;
    }

    public BetterBlockPattern build() {
        return new BetterBlockPattern(bakePredicates(), preserveUp);
    }

    protected Predicate<CachedBlockPosition>[][][] bakePredicates() {
        validate();
        var sizeY = sidewaysLayers ? depth : layers.size();
        var sizeZ = sidewaysLayers ? layers.size() : depth;
        @SuppressWarnings("unchecked")
        var predicates = (Predicate<CachedBlockPosition>[][][]) new Predicate[width][sizeY][sizeZ];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < layers.size(); y++) {
                for (int z = 0; z < depth; z++) {
                    if (sidewaysLayers) {
                        predicates[x][z][y] = keys.get(layers.get(y)[z].charAt(x));
                    } else {
                        predicates[x][y][z] = keys.get(layers.get(y)[z].charAt(x));
                    }
                }
            }
        }

        return predicates;
    }

    protected void validate() {
        var missingValues = new ArrayList<String>();

        for (var mapping : keys.entrySet()) {
            if (mapping.getValue() == null) {
                missingValues.add(mapping.getKey().toString());
            }
        }

        Preconditions.checkState(missingValues.isEmpty(), "Missing mappings for keys: %s", String.join(", ", missingValues));
    }
}