package dev.falseresync.wizcraft.common.wand.focus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.api.common.report.MultiplayerReport;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.wand.focus.Focus;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusType;
import dev.falseresync.wizcraft.common.WizUtil;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.common.report.WizReports;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        return WizFocuses.CHARGING;
    }

    @Override
    public Item getItem() {
        return WizItems.CHARGING_FOCUS;
    }

    @Override
    public ActionResult use(World world, Wand wand, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            if (!world.isNight()
                    || world.getRainGradient(1) > 1
                    || world.getLightLevel(LightType.SKY, user.getBlockPos()) < world.getMaxLightLevel() * 0.5) {
                Report.trigger(player, WizReports.Wand.CANNOT_CHARGE);
                return ActionResult.FAIL;
            }

            if (user.raycast(WizUtil.findViewDistance(world) * 16, 0, true).getType()
                    != HitResult.Type.MISS) {
                Report.trigger(player, WizReports.Wand.CANNOT_CHARGE);
                return ActionResult.FAIL;
            }

            if (wand.isFullyCharged()) {
                Report.trigger(player, WizReports.Wand.ALREADY_FULLY_CHARGED);
                return ActionResult.PASS;
            }

            user.setCurrentHand(user.getActiveHand());
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void tick(World world, Wand wand, LivingEntity user, int remainingUseTicks) {
        super.tick(world, wand, user, remainingUseTicks);
        if (!world.isClient()) {
            chargingProgress += 1;
        }
    }

    @Override
    public void interrupt(World world, Wand wand, LivingEntity user, int remainingUseTicks) {
        super.interrupt(world, wand, user, remainingUseTicks);
        if (!world.isClient()) {
            chargingProgress = 0;
        }
    }

    @Override
    public void finish(World world, Wand wand, LivingEntity user) {
        super.finish(world, wand, user);
        if (user instanceof ServerPlayerEntity player) {
            chargingProgress = 0;
            wand.addCharge(40);
            MultiplayerReport.trigger((ServerWorld) world, player.getBlockPos(), player, WizReports.Wand.SUCCESSFULLY_CHARGED);
        }
    }

    public int getChargingProgress() {
        return chargingProgress;
    }
}
