package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.data.ItemBarComponent;
import falseresync.wizcraft.common.data.WizcraftComponents;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public abstract class FocusItem extends Item {
    private final Function<Item, Integer> rawIdGetter = Util.memoize(BuiltInRegistries.ITEM::getIdOrThrow);

    public FocusItem(Properties settings) {
        super(settings);
    }

    public int getRawId() {
        return rawIdGetter.apply(this);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        if (!stack.has(WizcraftComponents.UUID)) {
            stack.set(WizcraftComponents.UUID, UUID.randomUUID());
        }
    }

    protected final <T> void transferComponent(ItemStack sourceStack, ItemStack targetStack, DataComponentType<T> componentType) {
        targetStack.set(componentType, sourceStack.remove(componentType));
    }

    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, Player user) {
    }

    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, Player user) {
    }

    public InteractionResultHolder<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        return InteractionResultHolder.pass(wandStack);
    }

    public InteractionResult focusUseOnBlock(ItemStack wandStack, ItemStack focusStack, UseOnContext context) {
        return InteractionResult.PASS;
    }

    public InteractionResult focusUseOnEntity(ItemStack wandStack, ItemStack focusStack, Player user, LivingEntity entity, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public void focusUsageTick(Level world, LivingEntity user, ItemStack wandStack, ItemStack focusStack, int remainingUseTicks) {
    }

    public ItemStack focusFinishUsing(ItemStack wandStack, ItemStack focusStack, Level world, LivingEntity user) {
        return wandStack;
    }

    public void focusOnStoppedUsing(ItemStack wandStack, ItemStack focusStack, Level world, LivingEntity user, int remainingUseTicks) {
    }

    public void focusInventoryTick(ItemStack wandStack, ItemStack focusStack, Level world, Entity entity, int slot, boolean selected) {
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
        return wandStack.has(WizcraftComponents.ITEM_BAR);
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

    public void focusAppendTooltip(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
    }
}
