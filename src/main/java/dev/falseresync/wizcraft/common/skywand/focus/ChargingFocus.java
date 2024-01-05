package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.api.common.skywand.focus.FocusType;
import dev.falseresync.wizcraft.common.WizUtils;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.api.common.skywand.SkyWandData;
import dev.falseresync.wizcraft.common.report.WizReports;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class ChargingFocus extends Focus {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "charging");
    public static final Codec<ChargingFocus> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf("charging_progress", 0).forGetter(ChargingFocus::getChargingProgress)
            ).apply(instance, ChargingFocus::new));
    protected int chargingProgress = 0;

    public ChargingFocus() {
    }

    public ChargingFocus(int chargingProgress) {
        this.chargingProgress = chargingProgress;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public FocusType<ChargingFocus> getType() {
        return WizFocusTypes.CHARGING;
    }

    @Override
    public Item getItem() {
        return WizItems.CHARGING_FOCUS;
    }

    @Override
    public ActionResult use(World world, SkyWandData wand, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            if (!world.isNight()
                    || world.getRainGradient(1) > 1
                    || world.getLightLevel(LightType.SKY, user.getBlockPos()) < world.getMaxLightLevel() * 0.5) {
                Report.trigger(player, WizReports.CANNOT_CHARGE);
                return ActionResult.FAIL;
            }

            if (user.raycast(WizUtils.findViewDistance(world) * 16, 0, true).getType()
                    != HitResult.Type.MISS) {
                Report.trigger(player, WizReports.CANNOT_CHARGE);
                return ActionResult.FAIL;
            }

            if (wand.isFullyCharged()) {
                Report.trigger(player, WizReports.ALREADY_FULLY_CHARGED);
                return ActionResult.PASS;
            }

            user.setCurrentHand(user.getActiveHand());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void tick(World world, SkyWandData wand, LivingEntity user, int remainingUseTicks) {
        super.tick(world, wand, user, remainingUseTicks);
        if (!world.isClient()) {
            chargingProgress += 1;
        }
    }

    @Override
    public void interrupt(World world, SkyWandData wand, LivingEntity user, int remainingUseTicks) {
        super.interrupt(world, wand, user, remainingUseTicks);
        if (!world.isClient()) {
            chargingProgress = 0;
        }
    }

    @Override
    public void finish(World world, SkyWandData wand, LivingEntity user) {
        super.finish(world, wand, user);
        if (user instanceof ServerPlayerEntity player) {
            chargingProgress = 0;
            wand.addCharge(40);
            Report.trigger(player, WizReports.SUCCESSFULLY_CHARGED);
        }
    }

    public int getChargingProgress() {
        return chargingProgress;
    }
}
