package dev.falseresync.wizcraft.common.skywand.focus;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.entity.StarProjectileEntity;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.CommonReports;
import dev.falseresync.wizcraft.common.skywand.SkyWandData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StarshooterFocus extends Focus {
    public static final int DEFAULT_COST = 2;
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "starshooter");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public FocusType<StarshooterFocus> getType() {
        return WizFocusTypes.STARSHOOTER;
    }

    @Override
    public Item getItem() {
        return WizItems.STARSHOOTER_FOCUS;
    }

    @Override
    public ActionResult use(World world, SkyWandData wand, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " attempts to use a starshooter focus");

        if (user instanceof PlayerEntity player) {
            if (!wand.tryExpendCharge(DEFAULT_COST, player)) {
                CommonReports.insufficientCharge(world, user);
                return ActionResult.FAIL;
            }

            world.spawnEntity(new StarProjectileEntity(user, world));
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
