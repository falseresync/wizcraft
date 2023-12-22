package dev.falseresync.common.skywand.focus;

import dev.falseresync.common.Wizcraft;
import dev.falseresync.common.entity.StarProjectileEntity;
import dev.falseresync.common.skywand.SkyWand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class StarshooterFocus extends Focus {
    public StarshooterFocus(NbtCompound nbt) {

    }

    @Override
    public Identifier getId() {
        return new Identifier(Wizcraft.MODID, "starshooter");
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, ItemStack stack, SkyWand wand, LivingEntity user, Hand hand) {
        Wizcraft.LOGGER.trace("Attempting to fire with a starshooter focus");
        var charge = wand.getCharge();
        if (charge >= 10) {
            wand.expendCharge(10);
            stack = wand.toStack();
            var direction = user.getCameraPosVec(1).add(user.getRotationVec(1));

            var projectile = Util.make(new StarProjectileEntity(user, direction.x, direction.y, direction.z, world), (entity) -> {
//                entity.setVelocity(user, pitch, yaw, 0, 2, 1);
            });
            world.spawnEntity(projectile);
        }
        return TypedActionResult.pass(stack);
    }
}
