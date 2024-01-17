package dev.falseresync.wizcraft.common.wand.focus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.common.wand.focus.Focus;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusType;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.report.WizReports;
import dev.falseresync.wizcraft.compat.anshar.AnsharCompat;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CometWarpFocus extends Focus {
    public static final int DEFAULT_PLACEMENT_COST = 5;
    public static final int DEFAULT_WARPING_COST = 15;
    public static final int DEFAULT_INTERDIMENSIONAL_COST = 30;
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "comet_warp");
    public static final Codec<CometWarpFocus> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    GlobalPos.CODEC.optionalFieldOf("anchor", null).forGetter(CometWarpFocus::getAnchor)
            ).apply(instance, CometWarpFocus::new));

    @Nullable
    protected GlobalPos anchor;

    public CometWarpFocus() {
    }

    public CometWarpFocus(@Nullable GlobalPos anchor) {
        this.anchor = anchor;
    }

    @Override
    public FocusType<CometWarpFocus> getType() {
        return WizFocuses.COMET_WARP;
    }

    @Override
    public Item getItem() {
        return WizItems.COMET_WARP_FOCUS;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void appendTooltip(@Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(world, tooltip, context);
        if (this.anchor == null) {
            tooltip.add(Text.translatable("tooltip.wizcraft.wand.setup_anchor")
                    .styled(style -> style.withColor(Formatting.GRAY)));
        } else {
            tooltip.add(Text.translatable(
                            "tooltip.wizcraft.wand.has_anchor",
                            this.anchor.getDimension().getValue().getPath(),
                            this.anchor.getPos().toShortString())
                    .styled(style -> style.withColor(Formatting.GRAY)));
        }
    }

    @Override
    public ActionResult use(World world, Wand wand, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            if (user.isSneaking()) {
                if (!wand.tryExpendCharge(DEFAULT_PLACEMENT_COST, player)) {
                    Report.trigger(player, WizReports.Wand.INSUFFICIENT_CHARGE);
                    return ActionResult.FAIL;
                }

                Report.trigger(player, WizReports.Focuses.ANCHOR_PLACED);
                this.anchor = GlobalPos.create(world.getRegistryKey(), user.getBlockPos());
            } else {
                if (this.anchor == null) {
                    Report.trigger(player, WizReports.Focuses.NO_ANCHOR);
                    return ActionResult.FAIL;
                }

                var destination = ((ServerWorld) world).getServer().getWorld(anchor.getDimension());
                if (destination == null) {
                    destination = ((ServerWorld) world);
                }

                var warpingCost = destination.getDimension() == world.getDimension()
                        ? DEFAULT_WARPING_COST
                        : DEFAULT_INTERDIMENSIONAL_COST;
                if (!wand.tryExpendCharge(warpingCost, player)) {
                    Report.trigger(player, WizReports.Wand.INSUFFICIENT_CHARGE);
                    return ActionResult.FAIL;
                }

                if (AnsharCompat.get().tryEnterNetwork(world, anchor.getPos(), player)) {
                    return ActionResult.SUCCESS;
                }

                Report.trigger(player, WizReports.Focuses.TELEPORTED);
                FabricDimensions.teleport(
                        user, destination,
                        new TeleportTarget(anchor.getPos().toCenterPos(), Vec3d.ZERO, user.getYaw(), user.getPitch()));
                anchor = null;
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    public @Nullable GlobalPos getAnchor() {
        return anchor;
    }
}
