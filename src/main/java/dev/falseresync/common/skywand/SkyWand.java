package dev.falseresync.common.skywand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.common.WizRegistries;
import dev.falseresync.common.Wizcraft;
import dev.falseresync.common.item.FocusItem;
import dev.falseresync.common.item.SkyWandItem;
import dev.falseresync.common.item.WizItems;
import dev.falseresync.common.skywand.focus.Focus;
import dev.falseresync.common.skywand.focus.WizFocuses;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class SkyWand {
    public static final Codec<SkyWand> CODEC;
    protected int maxCharge;
    protected int charge;
    protected Focus activeFocus;
    protected final Set<ItemStack> focuses = new HashSet<>();

    protected static final String KEY_SKY_WAND = "SkyWand";
    protected static final String KEY_MAX_CHARGE = "MaxCharge";
    protected static final String KEY_CHARGE = "Charge";
    protected static final String KEY_ACTIVE_FOCUS = "ActiveFocus";
    protected static final String KEY_FOCUSES = "Focuses";

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf(KEY_MAX_CHARGE, 100)
                        .forGetter(SkyWand::getMaxCharge),
                Codec.INT.optionalFieldOf(KEY_CHARGE, 0)
                        .forGetter(SkyWand::getCharge),
                Focus.CODEC.optionalFieldOf(KEY_ACTIVE_FOCUS, WizFocuses.CHARGING)
                        .forGetter(SkyWand::getActiveFocus)
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
}
