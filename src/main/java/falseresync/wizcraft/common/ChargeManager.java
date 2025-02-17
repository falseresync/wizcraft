package falseresync.wizcraft.common;

import com.google.common.base.*;
import falseresync.wizcraft.common.data.attachment.*;
import falseresync.wizcraft.common.data.component.*;
import net.fabricmc.fabric.api.event.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import org.jetbrains.annotations.*;

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

    public ChargeManager() {
        WAND_CHARGE_SPENT.register((wandStack, cost, user) -> {
            if (user != null && user.hasAttached(WizcraftAttachments.MAX_CHARGE_IN_SHELLS)) {
                var shellsCurrent = user.getAttachedOrCreate(WizcraftAttachments.CHARGE_IN_SHELLS);
                var wandCurrent = wandStack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0);
                var wandMax = wandStack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
                var transferred = Math.min(shellsCurrent, wandMax - wandCurrent);
                user.modifyAttached(WizcraftAttachments.CHARGE_IN_SHELLS, it -> it - transferred);
                wandStack.apply(WizcraftComponents.WAND_CHARGE, 0, it -> it + transferred);
            }
        });

        WAND_OVERCHARGED.register((wandStack, excess, user) -> {
            if (user != null && user.hasAttached(WizcraftAttachments.MAX_CHARGE_IN_SHELLS)) {
                Wizcraft.getChargeManager().applyShellCharge(user, excess);
            }
        });
    }

    public boolean areShellsFull(PlayerEntity player) {
        if (player.hasAttached(WizcraftAttachments.MAX_CHARGE_IN_SHELLS)) {
            return player.getAttachedOrCreate(WizcraftAttachments.MAX_CHARGE_IN_SHELLS) <= player.getAttachedOrCreate(WizcraftAttachments.CHARGE_IN_SHELLS);
        }

        return true;
    }

    public void applyShellCharge(PlayerEntity player, int amount) {
        var current = player.getAttachedOrCreate(WizcraftAttachments.CHARGE_IN_SHELLS);
        if (amount >= 0) {
            var max = player.getAttachedOrCreate(WizcraftAttachments.MAX_CHARGE_IN_SHELLS);
            player.setAttached(WizcraftAttachments.CHARGE_IN_SHELLS, Math.min(current + amount, max));
        } else {
            player.setAttached(WizcraftAttachments.CHARGE_IN_SHELLS, Math.min(current + amount, 0));
        }
    }

    public boolean isWandFullyCharged(ItemStack wandStack) {
        return wandStack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0) >= wandStack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
    }

    public boolean tryExpendWandCharge(ItemStack wandStack, int cost, @Nullable PlayerEntity user) {
        if (user != null && (user.isCreative() && WizcraftConfig.freeChargeInCreative || WizcraftConfig.freeChargeInSurvival)) {
            return true;
        }
        var charge = wandStack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0);
        if (charge >= cost) {
            wandStack.apply(WizcraftComponents.WAND_CHARGE, charge, current -> current - cost);
            ChargeManager.WAND_CHARGE_SPENT.invoker().onWandChargeSpent(wandStack, cost, user);
            return true;
        }
        return false;
    }

    public void chargeWand(ItemStack wandStack, int amount, @Nullable PlayerEntity user) {
        Preconditions.checkArgument(amount > 0, "Use tryExpendCharge to subtract charge");
        var current = wandStack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0);
        var max = wandStack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
        wandStack.apply(WizcraftComponents.WAND_CHARGE, 0, it -> Math.min(it + amount, max));
        if (current + amount > max) {
            ChargeManager.WAND_OVERCHARGED.invoker().onWandOvercharged(wandStack, current + amount - max, user);
        }
    }

    @FunctionalInterface
    public interface WandExpend {
        void onWandChargeSpent(ItemStack wandStack, int cost, @Nullable PlayerEntity user);
    }

    @FunctionalInterface
    public interface WandOvercharge {
        void onWandOvercharged(ItemStack wandStack, int excess, @Nullable PlayerEntity user);
    }
}
