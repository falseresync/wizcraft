package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.entity.StarProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class StarshooterFocusItem extends FocusItem {
    public StarshooterFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (WizcraftItems.WAND.tryExpendCharge(wandStack, 2)) {
                world.spawnEntity(new StarProjectileEntity(user, world));
                return TypedActionResult.success(wandStack);
            }

//                Report.trigger(player, WizcraftReports.Wand.INSUFFICIENT_CHARGE);
            return TypedActionResult.fail(wandStack);
        }

        return super.focusUse(wandStack, focusStack, world, user, hand);
    }
}
