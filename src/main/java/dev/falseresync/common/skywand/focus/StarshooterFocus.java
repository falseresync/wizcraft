package dev.falseresync.common.skywand.focus;

import dev.falseresync.common.Wizcraft;
import dev.falseresync.common.entity.StarProjectileEntity;
import dev.falseresync.common.skywand.SkyWand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class StarshooterFocus extends Focus {
    private static final Identifier ID = new Identifier(Wizcraft.MODID, "starshooter");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ActionResult use(World world, SkyWand wand, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " attempts to use a starshooter focus");
        var charge = wand.getCharge();
        var cost = user instanceof PlayerEntity player && player.isCreative() ? 0 : 10;
        if (charge >= cost) {
            wand.expendCharge(cost);
            var projectile = new StarProjectileEntity(user, world);
            world.spawnEntity(projectile);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}
