package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ChargeShellItem extends Item {
    public static final int DEFAULT_CAPACITY = 100;

    public ChargeShellItem(Settings settings) {
        super(settings.component(WizcraftDataComponents.SHELL_CAPACITY, DEFAULT_CAPACITY));
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
            var capacity = stack.getOrDefault(WizcraftDataComponents.SHELL_CAPACITY, DEFAULT_CAPACITY);
            player.modifyAttached(WizcraftDataAttachments.MAX_CHARGE_IN_SHELLS, it -> it == null ? capacity : it + capacity);
            player.playSound(SoundEvents.ITEM_TRIDENT_RETURN);
            return ItemStack.EMPTY;
        }
        return super.finishUsing(stack, world, user);
    }
}
