package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.entity.EnergyVeilEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

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
            wandStack.set(WizcraftDataComponents.ENTITY_VEIL_ID, veil.getId());
            user.setCurrentHand(user.getActiveHand());
            return TypedActionResult.success(wandStack);
        }
        return TypedActionResult.pass(wandStack);
    }

    @Override
    public void focusUsageTick(World world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            Optional.ofNullable(wandStack.get(WizcraftDataComponents.ENTITY_VEIL_ID)).ifPresent(id -> {
                if (world.getEntityById(id) instanceof EnergyVeilEntity veil) {
                    veil.incrementLifeExpectancy(2);
                }
            });
        }
        // TODO: if there's not enough charge - bite the user
    }

    @Override
    public void focusOnStoppedUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
        focusFinishUsing(wandStack, focusStack, world, user);
    }

    @Override
    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user) {
        if (!world.isClient) {
            wandStack.remove(WizcraftDataComponents.ENTITY_VEIL_ID);
        }
        // TODO: if ran out of charge midway - explode the user
        return wandStack;
    }

    @Override
    public int focusGetMaxUseTime(ItemStack wandStack, ItemStack focusStack, LivingEntity user) {
        return 200;
    }
}
