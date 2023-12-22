package dev.falseresync.common.item;

import dev.falseresync.common.Wizcraft;
import dev.falseresync.common.skywand.SkyWand;
import dev.falseresync.common.skywand.focus.WizFocuses;
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
        Wizcraft.LOGGER.trace(user.getName() + " started using a wand");
        var stack = user.getStackInHand(hand);
        var wand = new SkyWand(stack);

        if (user.isSneaking()) {
            if (wand.shouldCharge()) {
                wand.switchFocus(WizItems.STARSHOOTER_FOCUS);
            } else {
                wand.switchFocus(WizFocuses.CHARGING);
            }
            return TypedActionResult.pass(wand.asStack());
        }

        var result = wand.getActiveFocus().use(world, wand, user);
        return new TypedActionResult<>(result, wand.asStack());
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 50;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        var wand = new SkyWand(stack);
        wand.getActiveFocus().tick(world, wand, user, remainingUseTicks);
        wand.saveDataToUnderlyingStack();
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Wizcraft.LOGGER.trace(user.getName() + " interrupted a wand usage");
        var wand = new SkyWand(stack);
        wand.getActiveFocus().interrupt(world, wand, user, remainingUseTicks);
        wand.saveDataToUnderlyingStack();
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " finished using a wand");
        var wand = new SkyWand(stack);
        wand.getActiveFocus().finish(world, wand, user);
        return wand.asStack();
    }
}
