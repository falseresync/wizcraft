package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.WizcraftUtil;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.common.Reports;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

public class ChargingFocusItem extends FocusItem {
    public ChargingFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (Wizcraft.getChargeManager().calculateEnvironmentCoefficient(world, player) <= 0.25f
                    || user.raycast(WizcraftUtil.findViewDistance(world) * 16, 0, true).getType() != HitResult.Type.MISS) {
                player.playSoundToPlayer(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.sendMessage(Text.translatable("hud.wizcraft.wand.cannot_charge"), true);
                return TypedActionResult.fail(wandStack);
            }

            if (Wizcraft.getChargeManager().cannotAddAnyCharge(wandStack, player)) {
                player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.sendMessage(Text.translatable("hud.wizcraft.wand.already_charged"), true);
                return TypedActionResult.pass(wandStack);
            }

            user.setCurrentHand(user.getActiveHand());
            wandStack.set(WizcraftComponents.CHARGING_FOCUS_PROGRESS, 0);
            return TypedActionResult.consume(wandStack);
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

            Reports.playSoundToEveryone(player, WizcraftSounds.SUCCESSFULLY_CHARGED);
            player.sendMessage(Text.translatable("hud.wizcraft.wand.successfully_charged").styled(style -> style.withColor(Formatting.GOLD)), true);
            var rotation = player.getRotationVec(1);
            var orthogonalDistance = 1;
            var sourcePos = player.getEyePos()
                    .add(player.getHandPosOffset(WizcraftItems.WAND))
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
        return ColorHelper.Argb.getArgb(0, 115, 190, 211);
    }
}
