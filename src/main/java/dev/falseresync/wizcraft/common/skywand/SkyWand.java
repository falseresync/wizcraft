package dev.falseresync.wizcraft.common.skywand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public class SkyWand {
    public static final Codec<SkyWand> CODEC;
    protected static final String KEY_SKY_WAND = "wizcraft:sky_wand";
    protected static final String KEY_MAX_CHARGE = "max_charge";
    protected static final String KEY_CHARGE = "charge";
    protected static final String KEY_FOCUS = "focus";

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf(KEY_MAX_CHARGE, 100).forGetter(SkyWand::getMaxCharge),
                Codec.INT.optionalFieldOf(KEY_CHARGE, 0).forGetter(SkyWand::getCharge),
                Focus.CODEC.optionalFieldOf(KEY_FOCUS, WizFocuses.CHARGING).forGetter(SkyWand::getFocus)
        ).apply(instance, SkyWand::new));
    }

    protected int maxCharge;
    protected int charge;
    protected Focus focus;

    protected SkyWand(int maxCharge, int charge, Focus focus) {
        this.maxCharge = maxCharge;
        this.charge = charge;
        this.focus = focus;
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
     *
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

    public void addCharge(int amount) {
        setCharge(charge + amount);
    }

    public void expendCharge(int amount) {
        setCharge(charge - amount);
    }

    public boolean tryExpendCharge(int amount, PlayerEntity user) {
        var cost = user.isCreative() ? 0 : amount;
        if (getCharge() >= cost) {
            expendCharge(cost);
            return true;
        }

        return false;
    }

    public Focus getFocus() {
        return focus;
    }

    public void switchFocus(Focus focus) {
        this.focus = focus;
    }
}
