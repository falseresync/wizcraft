package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.data.WizcraftComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ChargeShellItem extends Item {
    public static final int DEFAULT_CAPACITY = 100;

    public ChargeShellItem(Properties settings) {
        super(settings.component(WizcraftComponents.SHELL_CAPACITY, DEFAULT_CAPACITY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        var stack = user.getItemInHand(hand);
        var chargeShells = user.getAttached(WizcraftAttachments.CHARGE_SHELLS);
        if (chargeShells == null || chargeShells.canAddShell(DEFAULT_CAPACITY)) {
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return super.use(world, user, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof ServerPlayer player) {
            var capacity = stack.getOrDefault(WizcraftComponents.SHELL_CAPACITY, DEFAULT_CAPACITY);
            var chargeShells = player.getAttachedOrCreate(WizcraftAttachments.CHARGE_SHELLS).withShell(capacity);
            if (chargeShells != null) {
                player.setAttached(WizcraftAttachments.CHARGE_SHELLS, chargeShells);
                Reports.playSoundToEveryone(player, SoundEvents.TRIDENT_RETURN);
                return ItemStack.EMPTY;
            }
        }
        return super.finishUsingItem(stack, world, user);
    }
}
