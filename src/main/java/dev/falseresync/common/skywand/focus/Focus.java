package dev.falseresync.common.skywand.focus;

import dev.falseresync.lib.HasId;
import dev.falseresync.common.skywand.SkyWand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public abstract class Focus implements HasId {
    public ActionResult use(World world, SkyWand wand, LivingEntity user) {
        return ActionResult.PASS;
    }

    public void tick(World world, SkyWand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void interrupt(World world, SkyWand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void finish(World world, SkyWand wand, LivingEntity user) {
    }

    public void writeNbt(NbtCompound nbt) {
    }

    public void readNbt(NbtCompound nbt) {
    }
}
