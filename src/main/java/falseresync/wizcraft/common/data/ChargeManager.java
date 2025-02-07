package falseresync.wizcraft.common.data;

import com.google.common.base.Preconditions;
import falseresync.wizcraft.common.WizcraftConfig;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChargeManager {
    public static final Event<WandExpend> WAND_CHARGE_SPENT = EventFactory.createArrayBacked(WandExpend.class, listeners -> (wandStack, cost, user) -> {
        for (WandExpend listener : listeners) {
            listener.onWandChargeSpent(wandStack, cost, user);
        }
    });

    public static final Event<WandOvercharge> WAND_OVERCHARGED = EventFactory.createArrayBacked(WandOvercharge.class, listeners -> (wandStack, excess, user) -> {
        for (WandOvercharge listener : listeners) {
            listener.onWandOvercharged(wandStack, excess, user);
        }
    });

    @FunctionalInterface
    public interface WandExpend {
        void onWandChargeSpent(ItemStack wandStack, int cost, @Nullable PlayerEntity user);
    }

    @FunctionalInterface
    public interface WandOvercharge {
        void onWandOvercharged(ItemStack wandStack, int excess, @Nullable PlayerEntity user);
    }

    static {
        WAND_CHARGE_SPENT.register((wandStack, cost, user) -> {
            if (user != null && user.hasAttached(WizcraftDataAttachments.MAX_CHARGE_IN_SHELLS)) {
                var shellsCurrent = user.getAttachedOrCreate(WizcraftDataAttachments.CHARGE_IN_SHELLS);
                var wandCurrent = wandStack.getOrDefault(WizcraftDataComponents.WAND_CHARGE, 0);
                var wandMax = wandStack.getOrDefault(WizcraftDataComponents.WAND_MAX_CHARGE, 0);
                var transferred = Math.min(shellsCurrent, wandMax - wandCurrent);
                user.modifyAttached(WizcraftDataAttachments.CHARGE_IN_SHELLS, it -> it - transferred);
                wandStack.apply(WizcraftDataComponents.WAND_CHARGE, 0, it -> it + transferred);
            }
        });

        WAND_OVERCHARGED.register((wandStack, excess, user) -> {
            if (user != null && user.hasAttached(WizcraftDataAttachments.MAX_CHARGE_IN_SHELLS)) {
                applyShellCharge(user, excess);
            }
        });
    }

    public static boolean areShellsFull(PlayerEntity player) {
        if (player.hasAttached(WizcraftDataAttachments.MAX_CHARGE_IN_SHELLS)) {
            return player.getAttachedOrCreate(WizcraftDataAttachments.MAX_CHARGE_IN_SHELLS) <= player.getAttachedOrCreate(WizcraftDataAttachments.CHARGE_IN_SHELLS);
        }

        return true;
    }

    public static void applyShellCharge(PlayerEntity player, int amount) {
        var current = player.getAttachedOrCreate(WizcraftDataAttachments.CHARGE_IN_SHELLS);
        if (amount >= 0) {
            var max = player.getAttachedOrCreate(WizcraftDataAttachments.MAX_CHARGE_IN_SHELLS);
            player.setAttached(WizcraftDataAttachments.CHARGE_IN_SHELLS, Math.min(current + amount, max));
        } else {
            player.setAttached(WizcraftDataAttachments.CHARGE_IN_SHELLS, Math.min(current + amount, 0));
        }
    }

    public static boolean isWandFullyCharged(ItemStack wandStack) {
        return wandStack.getOrDefault(WizcraftDataComponents.WAND_CHARGE, 0) >= wandStack.getOrDefault(WizcraftDataComponents.WAND_MAX_CHARGE, 0);
    }

    public static boolean tryExpendWandCharge(ItemStack wandStack, int cost, @Nullable PlayerEntity user) {
        if (user != null && (user.isCreative() && WizcraftConfig.freeChargeInCreative || WizcraftConfig.freeChargeInSurvival)) {
            return true;
        }
        var charge = wandStack.getOrDefault(WizcraftDataComponents.WAND_CHARGE, 0);
        if (charge >= cost) {
            wandStack.apply(WizcraftDataComponents.WAND_CHARGE, charge, current -> current - cost);
            ChargeManager.WAND_CHARGE_SPENT.invoker().onWandChargeSpent(wandStack, cost, user);
            return true;
        }
        return false;
    }

    public static void chargeWand(ItemStack wandStack, int amount, @Nullable PlayerEntity user) {
        Preconditions.checkArgument(amount > 0, "Use tryExpendCharge to subtract charge");
        var current = wandStack.getOrDefault(WizcraftDataComponents.WAND_CHARGE, 0);
        var max = wandStack.getOrDefault(WizcraftDataComponents.WAND_MAX_CHARGE, 0);
        wandStack.apply(WizcraftDataComponents.WAND_CHARGE, 0, it -> Math.min(it + amount, max));
        if (current + amount > max) {
            ChargeManager.WAND_OVERCHARGED.invoker().onWandOvercharged(wandStack, current + amount - max, user);
        }
    }
}
