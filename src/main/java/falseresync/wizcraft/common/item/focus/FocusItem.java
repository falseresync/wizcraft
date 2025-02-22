package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.data.component.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.*;
import net.minecraft.registry.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import java.util.*;
import java.util.function.*;

public abstract class FocusItem extends Item {
    private final Function<Item, Integer> rawIdGetter = Util.memoize(Registries.ITEM::getRawIdOrThrow);

    public FocusItem(Settings settings) {
        super(settings);
    }

    public int getRawId() {
        return rawIdGetter.apply(this);
    }

    @Override
    public void postProcessComponents(ItemStack stack) {
        if (!stack.contains(WizcraftComponents.FOCUS_STACK_UUID)) {
            stack.set(WizcraftComponents.FOCUS_STACK_UUID, UUID.randomUUID());
        }
    }

    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
    }

    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
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
        return wandStack.contains(WizcraftComponents.ITEM_BAR);
    }

    public int focusGetItemBarStep(ItemStack wandStack, ItemStack focusStack) {
        return wandStack.getOrDefault(WizcraftComponents.ITEM_BAR, ItemBarComponent.DEFAULT).step();
    }

    public int focusGetItemBarColor(ItemStack wandStack, ItemStack focusStack) {
        return wandStack.getOrDefault(WizcraftComponents.ITEM_BAR, ItemBarComponent.DEFAULT).color();
    }

    public boolean focusHasGlint(ItemStack wandStack, ItemStack focusStack) {
        return false;
    }

    public void focusAppendTooltip(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    }
}
