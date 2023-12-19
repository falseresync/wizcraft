package dev.falseresync.common.item;

import dev.falseresync.common.Wizcraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Optional;

public class SkyWandItem extends Item {
    public SkyWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return TypedActionResult.pass(stack);
        }
        Wizcraft.LOGGER.trace(user.getName() + " started using a wand");

        if (!world.isNight() || world.isRaining() || world.getLightLevel(LightType.SKY, user.getBlockPos()) < world.getMaxLightLevel() * 0.5) {
            Wizcraft.LOGGER.trace("It's not night (%s) or it's raining (%s) or it's not lighty (%s)"
                    .formatted(!world.isNight(), world.isRaining(), world.getLightLevel(LightType.SKY, user.getBlockPos())));
            return TypedActionResult.fail(stack);
        }

        var viewDistance = ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.watchDistance;
        HitResult.Type hitType = null;
        if ((hitType = user.raycast(viewDistance * 16, 0, true).getType()) != HitResult.Type.MISS) {
            Wizcraft.LOGGER.trace("View distance (%s) is wack or raycast failed (%s)"
                    .formatted(viewDistance, hitType));
            return TypedActionResult.fail(stack);
        }

//        var userPos = user.getBlockPos();
//        var isOnSurface = BlockPos.stream(userPos.add(-1, 1, -1), userPos.add(1, 1, 1))
//                .map(posToCheck -> {
//                    var result1 = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, posToCheck);
//                    Wizcraft.LOGGER.info(String.valueOf(result1.getY()));
//                    return result1;
//                })
//                .anyMatch(surfacePos -> userPos.getY() > surfacePos.getY());
//        if (isOnSurface) {
//            user.sendMessage(Text.literal("User is not on surface"));
//            return TypedActionResult.fail(stack);
//        }

        // TODO(Charging mechanics):
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 100;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            Wizcraft.LOGGER.trace(user.getName() + " finished using a wand");
        } else {
            user.sendMessage(Text.literal("Hooray!"));
        }
        return stack;
    }
}
