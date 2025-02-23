package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.data.attachment.*;
import falseresync.wizcraft.common.data.component.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.network.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class ChargeShellItem extends Item {
    public static final int DEFAULT_CAPACITY = 100;

    public ChargeShellItem(Settings settings) {
        super(settings.component(WizcraftComponents.SHELL_CAPACITY, DEFAULT_CAPACITY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var chargeShells = user.getAttached(WizcraftAttachments.CHARGE_SHELLS);
        if (chargeShells == null || chargeShells.canAddShell(DEFAULT_CAPACITY)) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
        return super.use(world, user, hand);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 20;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            var capacity = stack.getOrDefault(WizcraftComponents.SHELL_CAPACITY, DEFAULT_CAPACITY);
            var chargeShells = player.getAttachedOrCreate(WizcraftAttachments.CHARGE_SHELLS).withShell(capacity);
            if (chargeShells != null) {
                player.setAttached(WizcraftAttachments.CHARGE_SHELLS, chargeShells);
                player.playSound(SoundEvents.ITEM_TRIDENT_RETURN);
                return ItemStack.EMPTY;
            }
        }
        return super.finishUsing(stack, world, user);
    }
}
