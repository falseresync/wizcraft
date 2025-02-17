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
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
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
            player.modifyAttached(WizcraftAttachments.MAX_CHARGE_IN_SHELLS, it -> it == null ? capacity : it + capacity);
            player.playSound(SoundEvents.ITEM_TRIDENT_RETURN);
            return ItemStack.EMPTY;
        }
        return super.finishUsing(stack, world, user);
    }
}
