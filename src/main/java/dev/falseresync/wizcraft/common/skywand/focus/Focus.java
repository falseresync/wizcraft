package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.common.WizRegistries;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.api.HasId;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Focus implements HasId {
    public static final String STACK_NBT_KEY = "Focus";
    public static final Codec<Focus> CODEC;

    static {
        CODEC = WizRegistries.FOCUSES.getCodec().dispatch(Focus::getType, Focus::getCodec);
    }

    protected String translationKey;

    public static Focus fromStack(ItemStack stack, Focus fallback) {
        return CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateSubNbt(STACK_NBT_KEY))
                .result().orElse(fallback);
    }

    public abstract Codec<? extends Focus> getCodec();

    /**
     * @implSpec Must return the same static instance, that's been registered
     * @implNote It's a tradeoff for not having a FocusType class
     */
    public abstract Focus getType();

    public abstract Item getItem();

    public ItemStack asStack() {
        var stack = new ItemStack(getItem());
        stack.setSubNbt(STACK_NBT_KEY, asNbt());
        return stack;
    }

    public NbtElement asNbt() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(Wizcraft.LOGGER::error).orElse(new NbtCompound());
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("focus", getId());
        }

        return this.translationKey;
    }

    public Text getName() {
        return Text.translatable(getTranslationKey());
    }

    public ActionResult use(World world, SkyWand wand, LivingEntity user) {
        return ActionResult.PASS;
    }

    public void tick(World world, SkyWand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void interrupt(World world, SkyWand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void finish(World world, SkyWand wand, LivingEntity user) {
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    }
}
