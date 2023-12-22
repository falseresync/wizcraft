package dev.falseresync.common.skywand.focus;

import dev.falseresync.api.HasId;
import dev.falseresync.common.skywand.SkyWand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class Focus implements HasId {
    public abstract TypedActionResult<ItemStack> use(World world, ItemStack stack, SkyWand wand, LivingEntity user, Hand hand);
}
