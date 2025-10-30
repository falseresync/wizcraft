package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.entity.StarProjectileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StarshooterFocusItem extends FocusItem {
    public StarshooterFocusItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer player) {
            if (Wizcraft.getChargeManager().tryExpendWandCharge(gadgetStack, 2, user)) {
                world.addFreshEntity(new StarProjectileEntity(user, world));
                focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
                return InteractionResultHolder.success(gadgetStack);
            }

            Reports.insufficientCharge(player);
            return InteractionResultHolder.fail(gadgetStack);
        }

        return super.focusUse(gadgetStack, focusStack, world, user, hand);
    }
}
