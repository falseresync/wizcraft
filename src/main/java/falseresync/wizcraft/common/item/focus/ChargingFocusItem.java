package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.data.component.*;
import falseresync.wizcraft.networking.report.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class ChargingFocusItem extends FocusItem {
    public ChargingFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (Wizcraft.getChargeManager().calculateEnvironmentCoefficient(world, player) <= 0.25f) {
                WizcraftReports.WAND_CANNOT_CHARGE.sendTo(player);
                return TypedActionResult.fail(wandStack);
            }

            if (user.raycast(WizcraftUtil.findViewDistance(world) * 16, 0, true).getType()
                    != HitResult.Type.MISS) {
                WizcraftReports.WAND_CANNOT_CHARGE.sendTo(player);
                return TypedActionResult.fail(wandStack);
            }

            if (Wizcraft.getChargeManager().cannotAddAnyCharge(wandStack, player)) {
                WizcraftReports.WAND_ALREADY_FULLY_CHARGED.sendTo(player);
                return TypedActionResult.pass(wandStack);
            }

            user.setCurrentHand(user.getActiveHand());
            wandStack.set(WizcraftComponents.CHARGING_FOCUS_PROGRESS, 0);
            return TypedActionResult.success(wandStack);
        }
        return super.focusUse(wandStack, focusStack, world, user, hand);
    }

    @Override
    public void focusUsageTick(World world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
        wandStack.apply(WizcraftComponents.CHARGING_FOCUS_PROGRESS, 0, current -> current + 1);
    }

    @Override
    public void focusOnStoppedUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
        wandStack.remove(WizcraftComponents.CHARGING_FOCUS_PROGRESS);
    }

    @Override
    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user) {
        wandStack.remove(WizcraftComponents.CHARGING_FOCUS_PROGRESS);
        if (user instanceof ServerPlayerEntity player) {
            var amount = (int) (30 * Wizcraft.getChargeManager().calculateEnvironmentCoefficient(world, player));
            Wizcraft.getChargeManager().chargeWand(wandStack, amount, player);
            focusStack.damage(1, player, EquipmentSlot.MAINHAND);
            WizcraftReports.WAND_SUCCESSFULLY_CHARGED.sendAround((ServerWorld) world, player.getBlockPos(), player);
        }
        return wandStack;
    }

    @Override
    public int focusGetMaxUseTime(ItemStack wandStack, ItemStack focusStack, LivingEntity user) {
        return 50;
    }

    @Override
    public boolean focusIsItemBarVisible(ItemStack wandStack, ItemStack focusStack) {
        return wandStack.getOrDefault(WizcraftComponents.CHARGING_FOCUS_PROGRESS, 0) > 0;
    }

    @Override
    public int focusGetItemBarStep(ItemStack wandStack, ItemStack focusStack) {
        return Math.round(wandStack.getOrDefault(WizcraftComponents.CHARGING_FOCUS_PROGRESS, 0) * 13f / focusGetMaxUseTime(wandStack, focusStack, null));
    }

    @Override
    public int focusGetItemBarColor(ItemStack wandStack, ItemStack focusStack) {
        return ColorHelper.Argb.getArgb(0, 115, 190, 211);
    }
}
