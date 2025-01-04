package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class ChargingFocusItem extends FocusItem{
    public ChargingFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (!world.isNight()
                    || world.getRainGradient(1) > 0.75
                    || world.getLightLevel(LightType.SKY, user.getBlockPos()) < world.getMaxLightLevel() * 0.5) {
//                Report.trigger(player, WizcraftReports.Wand.CANNOT_CHARGE);
                return TypedActionResult.fail(wandStack);
            }

            if (user.raycast(Wizcraft.findViewDistance(world) * 16, 0, true).getType()
                    != HitResult.Type.MISS) {
//                Report.trigger(player, WizcraftReports.Wand.CANNOT_CHARGE);
                return TypedActionResult.fail(wandStack);
            }

            if (WizcraftItems.WAND.isFullyCharged(wandStack)) {
//                Report.trigger(player, WizcraftReports.Wand.ALREADY_FULLY_CHARGED);
                return TypedActionResult.pass(wandStack);
            }

            user.setCurrentHand(user.getActiveHand());
            wandStack.set(WizcraftDataComponents.CHARGING_FOCUS_PROGRESS, 0);
            return TypedActionResult.success(wandStack);
        }
        return super.focusUse(wandStack, focusStack, world, user, hand);
    }

    @Override
    public void focusUsageTick(World world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
        wandStack.apply(WizcraftDataComponents.CHARGING_FOCUS_PROGRESS, 0, current -> current + 1);
    }

    @Override
    public void focusOnStoppedUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
        wandStack.remove(WizcraftDataComponents.CHARGING_FOCUS_PROGRESS);
    }

    @Override
    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user) {
        wandStack.remove(WizcraftDataComponents.CHARGING_FOCUS_PROGRESS);
        wandStack.apply(WizcraftDataComponents.WAND_CHARGE, 0, current -> current + 40);
        if (user instanceof ServerPlayerEntity player) {
//            MultiplayerReport.trigger((ServerWorld) world, player.getBlockPos(), player, WizcraftReports.Wand.SUCCESSFULLY_CHARGED);
        }
        return wandStack;
    }

    @Override
    public int focusGetMaxUseTime(ItemStack wandStack, ItemStack focusStack, LivingEntity user) {
        return 50;
    }

    @Override
    public boolean focusIsItemBarVisible(ItemStack wandStack, ItemStack focusStack) {
        return wandStack.getOrDefault(WizcraftDataComponents.CHARGING_FOCUS_PROGRESS, 0) > 0;
    }

    @Override
    public int focusGetItemBarStep(ItemStack wandStack, ItemStack focusStack) {
        return Math.round(wandStack.getOrDefault(WizcraftDataComponents.CHARGING_FOCUS_PROGRESS, 0) * 13f / focusGetMaxUseTime(wandStack, focusStack, null));
    }

    @Override
    public int focusGetItemBarColor(ItemStack wandStack, ItemStack focusStack) {
        return ColorHelper.Argb.getArgb(0, 115, 190, 211);
    }
}
