package falseresync.wizcraft.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

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

    public void focusUsageTick(World world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
    }

    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user) {
        return wandStack;
    }

    public void focusOnStoppedUsing(ItemStack wandStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
    }

    public void focusInventoryTick(ItemStack wandStack, ItemStack focusStack, World world, Entity entity, int slot, boolean selected) {
    }

    public boolean focusIsUsedOnRelease(ItemStack wandStack, ItemStack focusStack) {
        return false;
    }

    public int focusGetMaxUseTime(ItemStack wandStack, ItemStack focusStack, LivingEntity user) {
        return 0;
    }

    public float focusGetBonusAttackDamage(ItemStack wandStack, ItemStack focusStack, Entity target, float baseAttackDamage, DamageSource damageSource) {
        return 0F;
    }

    public boolean focusIsItemBarVisible(ItemStack wandStack, ItemStack focusStack) {
        return false;
    }

    public int focusGetItemBarStep(ItemStack wandStack, ItemStack focusStack) {
        return 0;
    }

    public int focusGetItemBarColor(ItemStack wandStack, ItemStack focusStack) {
        return 0;
    }

    public boolean focusHasGlint(ItemStack wandStack, ItemStack focusStack) {
        return false;
    }

    public void focusAppendTooltip(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    }
}
