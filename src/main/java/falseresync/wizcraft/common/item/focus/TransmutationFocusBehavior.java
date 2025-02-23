package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;

import java.util.*;

public class TransmutationFocusBehavior {
    private static final Random OFFSETS_RANDOM = Random.createLocal();
    private static final NavigableMap<Double, EntityHitBehavior> ACTIONS_ON_ENTITY_HIT = new TreeMap<>();
    private static double TOTAL_WEIGHT = 0;

    public static void register() {
        registerEntityHitBehavior(
                Wizcraft.getConfig().transmutation.agingWeight,
                (world, target, random) -> {
                    if (target.getType().isIn(WizcraftEntityTags.TRANSMUTATION_AGEABLE)
                            && target instanceof MobEntity mob) {
                        mob.setBaby(!mob.isBaby());
                        return ActionResult.SUCCESS;
                    }
                    return ActionResult.FAIL;
                }
        );
        registerEntityHitBehavior(
                Wizcraft.getConfig().transmutation.transformationWeight,
                (world, target, random) -> {
                    if (!target.getType().isIn(WizcraftEntityTags.TRANSMUTATION_TRANSFORMABLE)) {
                        return ActionResult.FAIL;
                    }
                    var entry = WizcraftUtil.nextRandomEntry(world, WizcraftEntityTags.TRANSMUTATION_RESULT, random);
                    if (entry.isEmpty()) {
                        return ActionResult.FAIL;
                    }
                    entry.get().spawn(world, target.getBlockPos(), SpawnReason.CONVERSION);
                    target.discard();
                    return ActionResult.SUCCESS;
                }
        );
        registerEntityHitBehavior(
                Wizcraft.getConfig().transmutation.doNothingWeight,
                (world, target, random) -> ActionResult.PASS
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

    public static Collection<EntityHitBehavior> viewWeightedRandomEntityHitBehaviors(Random random) {
        double value = random.nextDouble() * TOTAL_WEIGHT;
        return ACTIONS_ON_ENTITY_HIT.tailMap(value, false).values();
    }

    @FunctionalInterface
    public interface EntityHitBehavior {
        ActionResult onHit(ServerWorld world, LivingEntity target, Random random);
    }
}
