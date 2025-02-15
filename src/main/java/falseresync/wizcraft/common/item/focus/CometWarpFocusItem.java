package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.List;

public class CometWarpFocusItem extends FocusItem {
    public static final int DEFAULT_PLACEMENT_COST = 5;
    public static final int DEFAULT_WARPING_COST = 15;
    public static final int DEFAULT_INTERDIMENSIONAL_COST = 30;
    public CometWarpFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = focusStack.remove(WizcraftDataComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            wandStack.set(WizcraftDataComponents.WARP_FOCUS_ANCHOR, anchor);
        }
    }

    @Override
    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = wandStack.remove(WizcraftDataComponents.WARP_FOCUS_ANCHOR);
        focusStack.set(WizcraftDataComponents.WARP_FOCUS_ANCHOR, anchor);
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
                wandStack.set(WizcraftDataComponents.WARP_FOCUS_ANCHOR, GlobalPos.create(world.getRegistryKey(), user.getBlockPos()));
            } else {
                var anchor = wandStack.get(WizcraftDataComponents.WARP_FOCUS_ANCHOR);
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
                wandStack.remove(WizcraftDataComponents.WARP_FOCUS_ANCHOR);
            }
            return TypedActionResult.success(wandStack);
        }

        return TypedActionResult.consume(wandStack);
    }

    @Override
    public void focusAppendTooltip(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var anchor = wandStack.get(WizcraftDataComponents.WARP_FOCUS_ANCHOR);
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
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var anchor = stack.get(WizcraftDataComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            tooltip.add(Text.translatable(
                            "tooltip.wizcraft.wand.has_anchor",
                            anchor.dimension().getValue().getPath(),
                            anchor.pos().toShortString())
                    .styled(style -> style.withColor(Formatting.GRAY)));
        }
    }
}
