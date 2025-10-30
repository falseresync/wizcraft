package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.WizcraftUtil;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ChargingFocusItem extends FocusItem {
    public ChargingFocusItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer player) {
            if (Wizcraft.getChargeManager().calculateEnvironmentCoefficient(world, player) <= 0.25f
                    || user.pick(WizcraftUtil.findViewDistance(world) * 16, 0, true).getType() != HitResult.Type.MISS) {
                player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.translatable("hud.wizcraft.wand.cannot_charge"), true);
                return InteractionResultHolder.fail(wandStack);
            }

            if (Wizcraft.getChargeManager().cannotAddAnyCharge(wandStack, player)) {
                player.playNotifySound(SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.translatable("hud.wizcraft.wand.already_charged"), true);
                return InteractionResultHolder.pass(wandStack);
            }

            user.startUsingItem(user.getUsedItemHand());
            wandStack.set(WizcraftComponents.CHARGING_FOCUS_PROGRESS, 0);
            return InteractionResultHolder.consume(wandStack);
        }
        return super.focusUse(wandStack, focusStack, world, user, hand);
    }

    @Override
    public void focusUsageTick(Level world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
        wandStack.update(WizcraftComponents.CHARGING_FOCUS_PROGRESS, 0, current -> current + 1);
    }

    @Override
    public void focusOnStoppedUsing(ItemStack wandStack, ItemStack focusStack, Level world, LivingEntity user, int remainingUseTicks) {
        wandStack.remove(WizcraftComponents.CHARGING_FOCUS_PROGRESS);
    }

    @Override
    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, Level world, LivingEntity user) {
        wandStack.remove(WizcraftComponents.CHARGING_FOCUS_PROGRESS);
        if (user instanceof ServerPlayer player) {
            var amount = (int) (30 * Wizcraft.getChargeManager().calculateEnvironmentCoefficient(world, player));
            Wizcraft.getChargeManager().chargeWand(wandStack, amount, player);
            focusStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            Reports.playSoundToEveryone(player, WizcraftSounds.SUCCESSFULLY_CHARGED);
            player.displayClientMessage(Component.translatable("hud.wizcraft.wand.successfully_charged").withStyle(style -> style.withColor(ChatFormatting.GOLD)), true);
            var rotation = player.getViewVector(1);
            var orthogonalDistance = 1;
            var sourcePos = player.getEyePosition()
                    .add(player.getHandHoldingItemAngle(WizcraftItems.WAND))
                    .add(rotation.x * orthogonalDistance, -0.25, rotation.z * orthogonalDistance);
            Reports.addSparkles(world, sourcePos);
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
        return FastColor.ARGB32.color(0, 115, 190, 211);
    }
}
