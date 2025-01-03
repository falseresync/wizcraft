package falseresync.wizcraft.common.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class FocusItem extends Item {
    public FocusItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(wandStack);
    }

    public ActionResult focusUseOnBlock(ItemStack wandStack, ItemStack focusStack, ItemUsageContext context) {
        return ActionResult.PASS;
    }

    public ActionResult focusUseOnEntity(ItemStack wandStack, ItemStack focusStack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }
}
