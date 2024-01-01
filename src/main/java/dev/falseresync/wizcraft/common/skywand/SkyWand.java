package dev.falseresync.wizcraft.common.skywand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

import java.util.HashSet;
import java.util.Set;

public class SkyWand {
    public static final Codec<SkyWand> CODEC;
    protected int maxCharge;
    protected int charge;
    protected Focus activeFocus;

    protected static final String KEY_SKY_WAND = "SkyWand";
    protected static final String KEY_MAX_CHARGE = "MaxCharge";
    protected static final String KEY_CHARGE = "Charge";
    protected static final String KEY_ACTIVE_FOCUS = "ActiveFocus";

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf(KEY_MAX_CHARGE, 100).forGetter(SkyWand::getMaxCharge),
                Codec.INT.optionalFieldOf(KEY_CHARGE, 0).forGetter(SkyWand::getCharge),
                Focus.CODEC.optionalFieldOf(KEY_ACTIVE_FOCUS, WizFocuses.CHARGING).forGetter(SkyWand::getActiveFocus)
        ).apply(instance, SkyWand::new));
    }

    protected SkyWand(int maxCharge, int charge, Focus activeFocus) {
        this.maxCharge = maxCharge;
        this.charge = charge;
        this.activeFocus = activeFocus;

    }

    /**
     * Reads wand data from stack NBT. Doesn't modify or store the stack or irrelevant stack NBT
     */
    public static SkyWand fromStack(ItemStack stack) {
        var wandData = stack.getOrCreateSubNbt(KEY_SKY_WAND);
        var result = CODEC.parse(NbtOps.INSTANCE, wandData).resultOrPartial(Wizcraft.LOGGER::error);
        return result.orElseThrow();
    }

    /**
     * Save wand data to the passed stack's NBT. Modifies the passed stack and returns a copy of it
     * @return A copy of the modified passed stack with wand data attached
     */
    public ItemStack saveToStack(ItemStack stack) {
        var wandData = CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(Wizcraft.LOGGER::error).orElse(new NbtCompound());
        stack.setSubNbt(KEY_SKY_WAND, wandData);
        return stack.copy();
    }

    public int getMaxCharge() {
        return this.maxCharge;
    }

    public boolean isFullyCharged() {
        return this.charge >= getMaxCharge();
    }

    public int getCharge() {
        return this.charge;
    }

    protected void setCharge(int charge) {
        this.charge = Math.min(charge, getMaxCharge());
    }

    public void incrementCharge() {
        setCharge(this.charge + 60);
    }

    public void expendCharge(int amount) {
        setCharge(this.charge - amount);
    }

    public Focus getActiveFocus() {
        return this.activeFocus;
    }

    public void switchFocus(Focus focus) {
        this.activeFocus = focus;
    }
}
