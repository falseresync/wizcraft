package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EnergyVeilFocusItem extends FocusItem {
    public EnergyVeilFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            var veil = new EnergyVeilEntity(user, world);
            veil.setVeilWidth(4);
            veil.setVeilHeight(4);
            world.spawnEntity(veil);
            return TypedActionResult.success(wandStack);
        }
        return super.focusUse(wandStack, focusStack, world, user, hand);
    }

    @Override
    public void focusUsageTick(World world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {

        // if there's not enough charge - bite the user
        super.focusUsageTick(world, user, wandStack, focusStack, remainingUseTicks);
    }

    @Override
    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user) {
        // if ran out of charge midway - explode the user
        return super.focusFinishUsing(wandStack, focusStack, world, user);
    }

    @Override
    public int focusGetMaxUseTime(ItemStack wandStack, ItemStack focusStack, LivingEntity user) {
        return 1000;
    }
}
