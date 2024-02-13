package dev.falseresync.wizcraft.common.wand.focus;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.wand.focus.Focus;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusType;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.entity.StarProjectileEntity;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.common.report.WizcraftReports;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StarshooterFocus extends Focus {
    public static final int DEFAULT_COST = 2;
    public static final Identifier ID = new Identifier(Wizcraft.MOD_ID, "starshooter");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public FocusType<StarshooterFocus> getType() {
        return WizcraftFocuses.STARSHOOTER;
    }

    @Override
    public Item getItem() {
        return WizcraftItems.STARSHOOTER_FOCUS;
    }

    @Override
    public ActionResult use(World world, Wand wand, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            if (!wand.tryExpendCharge(DEFAULT_COST, player)) {
                Report.trigger(player, WizcraftReports.Wand.INSUFFICIENT_CHARGE);
                return ActionResult.FAIL;
            }

            world.spawnEntity(new StarProjectileEntity(user, world));
            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }
}
