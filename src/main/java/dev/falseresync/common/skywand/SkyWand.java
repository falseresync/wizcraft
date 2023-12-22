package dev.falseresync.common.skywand;

import dev.falseresync.common.WizRegistries;
import dev.falseresync.common.item.FocusItem;
import dev.falseresync.common.skywand.focus.Focus;
import dev.falseresync.common.skywand.focus.WizFocuses;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class SkyWand {
    protected final ItemStack underlyingStack;
    protected final NbtCompound data;
    protected int maxCharge = 100;
    protected int charge = 0;
    protected Focus activeFocus = WizFocuses.CHARGING;
    protected final Set<Focus> focuses = new HashSet<>();

    protected static final String KEY_MAX_CHARGE = "MaxCharge";
    protected static final String KEY_CHARGE = "Charge";
    protected static final String KEY_ACTIVE_FOCUS = "ActiveFocus";
    protected static final String KEY_FOCUSES = "Focuses";

    public SkyWand(ItemStack underlyingStack) {
        this.underlyingStack = underlyingStack;
        this.data = underlyingStack.getOrCreateNbt();

        if (data.contains("MaxCharge", NbtElement.INT_TYPE)) {
            maxCharge = data.getInt("MaxCharge");
        }
        if (data.contains("Charge", NbtElement.INT_TYPE)) {
            charge = data.getInt("Charge");
        }
        if (data.contains("ActiveFocus", NbtElement.STRING_TYPE)) {
            activeFocus = WizRegistries.FOCUSES.get(new Identifier(data.getString("ActiveFocus")));
        }
    }

    public int getMaxCharge() {
        return maxCharge;
    }

    public boolean isFullyCharged() {
        return charge >= getMaxCharge();
    }

    public int getCharge() {
        return charge;
    }

    protected void setCharge(int charge) {
        this.charge = Math.min(charge, getMaxCharge());
    }

    public void incrementCharge() {
        setCharge(charge + 60);
    }

    public void expendCharge(int amount) {
        setCharge(charge - amount);
    }

    public boolean shouldCharge() {
        return activeFocus.equals(WizFocuses.CHARGING);
    }

    public Focus getActiveFocus() {
        return activeFocus;
    }

    public void switchFocus(FocusItem focusItem) {
        activeFocus = focusItem.getFocus();
    }

    public void switchFocus(Focus focus) {
        activeFocus = focus;
    }

    /**
     * Save the wand data to underlying stack NBT and return the named stack
     *
     * @return modified underlying stack
     */
    public ItemStack asStack() {
        saveDataToUnderlyingStack();
        return underlyingStack;
    }

    public void saveDataToUnderlyingStack() {
        data.putInt(KEY_MAX_CHARGE, maxCharge);
        data.putInt(KEY_CHARGE, charge);
        data.putString(KEY_ACTIVE_FOCUS, activeFocus.getId().toString());

        underlyingStack.setNbt(data);
    }
}
