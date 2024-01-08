package dev.falseresync.wizcraft.api.common.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.falseresync.wizcraft.common.CommonKeys;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.common.wand.focus.Focus;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusStack;
import dev.falseresync.wizcraft.common.wand.focus.WizFocuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.MathHelper;

public class Wand {
    public static final Codec<Wand> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf(CommonKeys.MAX_CHARGE, 100).forGetter(Wand::getMaxCharge),
                    Codec.INT.optionalFieldOf(CommonKeys.CHARGE, 0).forGetter(Wand::getCharge),
                    FocusStack.CODEC.optionalFieldOf(CommonKeys.FOCUS_STACK, WizFocuses.CHARGING.defaultFocusStack())
                            .forGetter(Wand::getFocusStack)
            ).apply(instance, Wand::new));

    protected int maxCharge;
    protected int charge;
    protected FocusStack focusStack;

    protected Wand(int maxCharge, int charge, FocusStack focusStack) {
        this.maxCharge = maxCharge;
        this.charge = charge;
        this.focusStack = focusStack;
    }

    /**
     * Reads wand data from stack NBT. Doesn't modify or store the stack or irrelevant stack NBT
     */
    public static Wand fromStack(ItemStack stack) {
        return CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateSubNbt(CommonKeys.Namespaced.WAND))
                .resultOrPartial(Wizcraft.LOGGER::error).orElseThrow();
    }

    /**
     * Attach wand data to passed stack
     */
    public void attach(ItemStack stack) {
        stack.setSubNbt(CommonKeys.Namespaced.WAND, toNbt());
    }

    /**
     * Copy a stack and attach wand data to it
     */
    public ItemStack copyAndAttach(ItemStack stack) {
        var modified = stack.copy();
        modified.setSubNbt(CommonKeys.Namespaced.WAND, toNbt());
        return modified;
    }

    protected NbtElement toNbt() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(Wizcraft.LOGGER::error).orElse(new NbtCompound());
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
        this.charge = MathHelper.clamp(charge, 0, getMaxCharge());
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
        return focusStack.getFocus();
    }

    public FocusStack getFocusStack() {
        return focusStack;
    }

    public void switchFocus(FocusStack focusStack) {
        this.focusStack = focusStack;
    }
}
