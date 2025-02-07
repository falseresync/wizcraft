package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.data.ChargeManager;
import falseresync.wizcraft.common.entity.StarProjectileEntity;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class StarshooterFocusItem extends FocusItem {
    public StarshooterFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (ChargeManager.tryExpendWandCharge(wandStack, 2, user)) {
                world.spawnEntity(new StarProjectileEntity(user, world));
                return TypedActionResult.success(wandStack);
            }

            WizcraftReports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
            return TypedActionResult.fail(wandStack);
        }

        return super.focusUse(wandStack, focusStack, world, user, hand);
    }
}
