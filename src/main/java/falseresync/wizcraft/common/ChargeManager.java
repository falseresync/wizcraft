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
            if (user != null) {
                var chargeShells = user.getAttachedOrCreate(WizcraftAttachments.CHARGE_SHELLS);
                var wandCurrent = wandStack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0);
                var wandMax = wandStack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
                var compensation = wandMax - wandCurrent;
                var newShells = chargeShells.withChargeChange(-compensation);
                if (newShells != null) {
                    user.setAttached(WizcraftAttachments.CHARGE_SHELLS, newShells);
                    wandStack.apply(WizcraftComponents.WAND_CHARGE, 0, it -> it + compensation);
                }
            }
        });

        WAND_OVERCHARGED.register((wandStack, excess, user) -> {
            if (user != null) {
                Wizcraft.getChargeManager().applyShellCharge(user, excess);
            }
        });
    }

    public boolean areShellsFull(PlayerEntity player) {
        return player.getAttachedOrCreate(WizcraftAttachments.CHARGE_SHELLS).areShellsFull();
    }

    public void applyShellCharge(PlayerEntity player, int amount) {
        var newShells = player.getAttachedOrCreate(WizcraftAttachments.CHARGE_SHELLS).withChargeChange(amount);
        if (newShells != null) {
            player.setAttached(WizcraftAttachments.CHARGE_SHELLS, newShells);
        }
    }

    public boolean isWandFullyCharged(ItemStack wandStack) {
        return wandStack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0) >= wandStack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
    }

    public boolean tryExpendWandCharge(ItemStack wandStack, int cost, @Nullable PlayerEntity user) {
        if (user != null && (user.isCreative() && Wizcraft.getConfig().infiniteCharge.isCreativeOnly() || Wizcraft.getConfig().infiniteCharge.isAlways())) {
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
