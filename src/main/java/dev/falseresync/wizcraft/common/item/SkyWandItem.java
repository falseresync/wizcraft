package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import dev.falseresync.wizcraft.lib.HasId;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SkyWandItem extends Item implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "sky_wand");

    public SkyWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Wizcraft.LOGGER.trace(user.getName() + " started using a wand");
        var stack = user.getStackInHand(hand);
        var wand = SkyWand.fromStack(stack);
        var result = wand.getActiveFocus().use(world, wand, user);
        return new TypedActionResult<>(result, wand.saveToStack(stack));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 50;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        var wand = SkyWand.fromStack(stack);
        wand.getActiveFocus().tick(world, wand, user, remainingUseTicks);
        wand.saveToStack(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Wizcraft.LOGGER.trace(user.getName() + " interrupted a wand usage");
        var wand = SkyWand.fromStack(stack);
        wand.getActiveFocus().interrupt(world, wand, user, remainingUseTicks);
        wand.saveToStack(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " finished using a wand");
        var wand = SkyWand.fromStack(stack);
        wand.getActiveFocus().finish(world, wand, user);
        return wand.saveToStack(stack);
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
