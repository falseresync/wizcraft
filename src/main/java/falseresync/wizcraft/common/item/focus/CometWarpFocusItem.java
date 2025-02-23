package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.data.component.*;
import falseresync.wizcraft.networking.report.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class CometWarpFocusItem extends FocusItem {
    public static final int DEFAULT_PLACEMENT_COST = 5;
    public static final int DEFAULT_WARPING_COST = 15;
    public static final int DEFAULT_INTERDIMENSIONAL_COST = 30;

    public CometWarpFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = focusStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            wandStack.set(WizcraftComponents.WARP_FOCUS_ANCHOR, anchor);
        }
    }

    @Override
    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
        focusStack.set(WizcraftComponents.WARP_FOCUS_ANCHOR, anchor);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (user.isSneaking()) {
                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, DEFAULT_PLACEMENT_COST, user)) {
                    WizcraftReports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
                    return TypedActionResult.fail(wandStack);
                }

                WizcraftReports.COMET_WARP_ANCHOR_PLACED.sendTo(player);
                var globalPos = GlobalPos.create(world.getRegistryKey(), user.getBlockPos());
                wandStack.set(WizcraftComponents.WARP_FOCUS_ANCHOR, globalPos);
                if (world.random.nextFloat() < 0.1f) {
                    focusStack.damage(1, player, EquipmentSlot.MAINHAND);
                }
            } else {
                var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
                if (anchor == null) {
                    WizcraftReports.COMET_WARP_NO_ANCHOR.sendTo(player);
                    return TypedActionResult.fail(wandStack);
                }

                var destination = ((ServerWorld) world).getServer().getWorld(anchor.dimension());
                if (destination == null) {
                    destination = ((ServerWorld) world);
                }

                var warpingCost = destination.getDimension() != world.getDimension()
                        ? DEFAULT_INTERDIMENSIONAL_COST
                        : DEFAULT_WARPING_COST;
                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, warpingCost, user)) {
                    WizcraftReports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
                    return TypedActionResult.fail(wandStack);
                }

                WizcraftReports.COMET_WARP_TELEPORTED.sendTo(player);
                user.teleportTo(new TeleportTarget(destination, anchor.pos().toCenterPos(), Vec3d.ZERO, user.getYaw(), user.getPitch(), TeleportTarget.NO_OP));
                wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
                focusStack.damage(1, player, EquipmentSlot.MAINHAND);
            }
            return TypedActionResult.success(wandStack);
        }

        return TypedActionResult.consume(wandStack);
    }

    @Override
    public boolean focusHasGlint(ItemStack wandStack, ItemStack focusStack) {
        return hasGlint(wandStack);
    }

    @Override
    public void focusAppendTooltip(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
        if (anchor == null) {
            tooltip.add(Text.translatable("tooltip.wizcraft.wand.setup_anchor")
                    .styled(style -> style.withColor(Formatting.GRAY)));
        } else {
            tooltip.add(Text.translatable(
                            "tooltip.wizcraft.wand.has_anchor",
                            anchor.dimension().getValue().getPath(),
                            anchor.pos().toShortString())
                    .styled(style -> style.withColor(Formatting.GRAY)));
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.contains(WizcraftComponents.WARP_FOCUS_ANCHOR);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var anchor = stack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            tooltip.add(Text.translatable(
                            "tooltip.wizcraft.wand.has_anchor",
                            anchor.dimension().getValue().getPath(),
                            anchor.pos().toShortString())
                    .styled(style -> style.withColor(Formatting.GRAY)));
        }
    }
}
