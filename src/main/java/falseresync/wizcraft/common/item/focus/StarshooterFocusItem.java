package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.entity.*;
import falseresync.wizcraft.networking.report.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class StarshooterFocusItem extends FocusItem {
    public StarshooterFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, 2, user)) {
                world.spawnEntity(new StarProjectileEntity(user, world));
                focusStack.damage(1, user, EquipmentSlot.MAINHAND);
                return TypedActionResult.success(wandStack);
            }

            WizcraftReports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
            return TypedActionResult.fail(wandStack);
        }

        return super.focusUse(wandStack, focusStack, world, user, hand);
    }
}
