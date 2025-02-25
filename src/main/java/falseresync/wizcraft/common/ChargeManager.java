package falseresync.wizcraft.common;

import com.google.common.base.*;
import falseresync.wizcraft.common.config.*;
import falseresync.wizcraft.common.data.attachment.*;
import falseresync.wizcraft.common.data.component.*;
import net.fabricmc.fabric.api.event.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
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
                // Maybe only compensate the cost? But that would be confusing
                var chargeShells = user.getAttached(WizcraftAttachments.CHARGE_SHELLS);
                if (chargeShells == null) {
                    return;
                }

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
        //noinspection DataFlowIssue
        return player.hasAttached(WizcraftAttachments.CHARGE_SHELLS)
                && player.getAttached(WizcraftAttachments.CHARGE_SHELLS).areShellsFull();
    }

    public void applyShellCharge(PlayerEntity player, int amount) {
        var shells = player.getAttached(WizcraftAttachments.CHARGE_SHELLS);
        if (shells == null) {
            return;
        }
        var newShells = shells.withChargeChange(amount);
        if (newShells != null) {
            player.setAttached(WizcraftAttachments.CHARGE_SHELLS, newShells);
        }
    }

    public boolean isWandFullyCharged(ItemStack wandStack) {
        return wandStack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0) >= wandStack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
    }

    public boolean cannotAddAnyCharge(ItemStack wandStack, PlayerEntity player) {
        return isWandFullyCharged(wandStack) && areShellsFull(player);
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

    public void tryChargeWandPassively(ItemStack wandStack, World world, PlayerEntity player) {
        if (Wizcraft.getChargeManager().cannotAddAnyCharge(wandStack, player)) {
            return;
        }

        var config = Wizcraft.getConfig().passiveCharge;
        if (config == WizcraftConfig.PassiveCharge.DISABLED) {
            return;
        }

        var usageCoefficient = ItemStack.areEqual(player.getMainHandStack(), wandStack) ? 1f : 0.25f;
        var passiveChargingThreshold = Math.clamp(0.005f * calculateEnvironmentCoefficient(world, player) * config.coefficient * usageCoefficient, 0, 0.1f);

        // At most 10% of the time, i.e. up to 2 times per second
        if (world.random.nextFloat() < passiveChargingThreshold) {
            Wizcraft.getChargeManager().chargeWand(wandStack, 1, player);
        }
    }

    public float calculateEnvironmentCoefficient(World world, PlayerEntity player) {
        var environmentCoefficient = 1f;
        var worldType = world.getRegistryKey();
        if (worldType == World.NETHER) {
            environmentCoefficient *= 0.1f;
        } else if (worldType == World.END) {
            environmentCoefficient *= 3f;
        } else {
            environmentCoefficient *= world.isNight() ? 1 : 0.5f;
            environmentCoefficient *= world.getBiome(player.getBlockPos()).value().hasPrecipitation() ? 1 - world.getRainGradient(1) : 1;
            environmentCoefficient *= world.getLightLevel(LightType.SKY, player.getBlockPos()) / (world.getMaxLightLevel() * 0.5f);
        }
        return environmentCoefficient;
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
