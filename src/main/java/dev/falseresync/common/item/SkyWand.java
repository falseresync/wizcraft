package dev.falseresync.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class SkyWand {
    protected final ItemStack underlyingStack;
    protected final NbtCompound data;
    protected int maxCharge = 100;
    protected int charge = 0;

    public SkyWand(ItemStack underlyingStack) {
        this.underlyingStack = underlyingStack;
        this.data = underlyingStack.getOrCreateNbt();

        if (data.contains("maxCharge", NbtElement.INT_TYPE)) {
            maxCharge = data.getInt("maxCharge");
        }
        if (data.contains("charge", NbtElement.INT_TYPE)) {
            charge = data.getInt("charge");
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

    /**
     * Save the wand data to underlying stack NBT and return the named stack
     *
     * @return modified underlying stack
     */
    public ItemStack toStack() {
        saveData();
        return underlyingStack;
    }

    public void saveData() {
        data.putInt("maxCharge", maxCharge);
        data.putInt("charge", charge);
        underlyingStack.setNbt(data);
    }
}
