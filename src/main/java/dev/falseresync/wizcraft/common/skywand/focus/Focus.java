package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.common.WizRegistries;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.api.common.HasId;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Focus implements HasId {
    public static final String KEY_FOCUS = "wizcraft:focus";
    public static final Codec<Focus> CODEC;

    static {
        CODEC = WizRegistries.FOCUSES.getCodec().dispatch(Focus::getType, Focus::getCodec);
    }

    @Nullable
    protected String translationKey;
    @Nullable
    protected NbtCompound cachedStackNbt;

    public static Focus fromStack(ItemStack stack, Focus fallback) {
        var stackNbt = stack.getOrCreateNbt();
        var focus = CODEC.parse(NbtOps.INSTANCE, stackNbt.get(KEY_FOCUS))
                .resultOrPartial(Wizcraft.LOGGER::error).orElse(fallback);
        focus.setCachedStackNbt(stackNbt);
        return focus;
    }

    public abstract Codec<? extends Focus> getCodec();

    /**
     * @implSpec Must return the same static instance, that's been registered
     * @implNote It's a tradeoff for not having a FocusType class
     */
    public abstract Focus getType();

    public abstract Item getItem();

    public ItemStack toStack() {
        var nbt = getCachedStackNbt();
        nbt.put(KEY_FOCUS, toNbt());

        var stack = new ItemStack(getItem());
        stack.setNbt(nbt);
        return stack;
    }

    public NbtElement toNbt() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(Wizcraft.LOGGER::error).orElseGet(NbtCompound::new);
    }
    public String getTranslationKey() {
        if (translationKey == null) {
            translationKey = Util.createTranslationKey("focus", getId());
        }

        return translationKey;
    }

    public NbtCompound getCachedStackNbt() {
        if (cachedStackNbt == null) {
            cachedStackNbt = new NbtCompound();
        }

        return cachedStackNbt;
    }

    @ApiStatus.Internal
    public void setCachedStackNbt(@Nullable NbtCompound cachedStackNbt) {
        this.cachedStackNbt = cachedStackNbt;
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

    public void appendTooltip(@Nullable World world, List<Text> tooltip, TooltipContext context) {
    }

    @ApiStatus.Internal
    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        List<Text> tooltip = new ArrayList<>();
        appendTooltip(player == null ? null : player.getWorld(), tooltip, context);
        return tooltip;
    }
}
