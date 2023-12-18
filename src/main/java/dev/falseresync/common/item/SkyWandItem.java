package dev.falseresync.common.item;

import dev.falseresync.common.Wizcraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SkyWandItem extends Item {
    public SkyWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        if (!world.isNight() || world.isRaining() || !world.isSkyVisible(user.getBlockPos())) {
            return TypedActionResult.fail(stack);
        }

        // TODO(Charging mechanics):
        if (true) {
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
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
        Wizcraft.LOGGER.info("Finished using a wand");
        return stack;
    }
}
