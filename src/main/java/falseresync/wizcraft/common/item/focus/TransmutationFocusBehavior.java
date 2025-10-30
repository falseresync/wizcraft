package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftUtil;
import falseresync.wizcraft.common.entity.WizcraftEntityTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TransmutationFocusBehavior {
    private static final RandomSource OFFSETS_RANDOM = RandomSource.createNewThreadLocalInstance();
    private static final NavigableMap<Double, EntityHitBehavior> ACTIONS_ON_ENTITY_HIT = new TreeMap<>();
    private static double TOTAL_WEIGHT = 0;

    public static void register() {
        registerEntityHitBehavior(
                Wizcraft.getConfig().transmutation.agingWeight,
                (world, target, random) -> {
                    if (target.getType().is(WizcraftEntityTags.TRANSMUTATION_AGEABLE)
                            && target instanceof Mob mob) {
                        mob.setBaby(!mob.isBaby());
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.FAIL;
                }
        );
        registerEntityHitBehavior(
                Wizcraft.getConfig().transmutation.transformationWeight,
                (world, target, random) -> {
                    if (!target.getType().is(WizcraftEntityTags.TRANSMUTATION_TRANSFORMABLE)) {
                        return InteractionResult.FAIL;
                    }
                    var entry = WizcraftUtil.nextRandomEntry(world, WizcraftEntityTags.TRANSMUTATION_RESULT, random);
                    if (entry.isEmpty()) {
                        return InteractionResult.FAIL;
                    }
                    entry.get().spawn(world, target.blockPosition(), MobSpawnType.CONVERSION);
                    target.discard();
                    return InteractionResult.SUCCESS;
                }
        );
        registerEntityHitBehavior(
                Wizcraft.getConfig().transmutation.doNothingWeight,
                (world, target, random) -> InteractionResult.PASS
        );
    }

    public static void registerEntityHitBehavior(int weight, EntityHitBehavior action) {
        if (weight <= 0) {
            return;
        }
        // This is to ensure there are no lost actions because of duplicate keys
        var offset = OFFSETS_RANDOM.nextDouble() / 10000;
        ACTIONS_ON_ENTITY_HIT.put(weight + offset, action);
        TOTAL_WEIGHT += weight + offset;
    }

    public static Collection<EntityHitBehavior> viewWeightedRandomEntityHitBehaviors(RandomSource random) {
        double value = random.nextDouble() * TOTAL_WEIGHT;
        return ACTIONS_ON_ENTITY_HIT.tailMap(value, false).values();
    }

    @FunctionalInterface
    public interface EntityHitBehavior {
        InteractionResult onHit(ServerLevel world, LivingEntity target, RandomSource random);
    }
}
