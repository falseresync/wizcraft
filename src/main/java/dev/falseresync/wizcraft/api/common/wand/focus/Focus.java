package dev.falseresync.wizcraft.api.common.wand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.api.WizcraftRegistries;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Focus implements HasId {
    public abstract FocusType<? extends Focus> getType();

    public abstract Item getItem();

    public int getMaxUsageTicks(Wand wand) {
        return 50;
    }

    public ActionResult use(World world, Wand wand, LivingEntity user) {
        return ActionResult.PASS;
    }

    public void tick(World world, Wand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void interrupt(World world, Wand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void finish(World world, Wand wand, LivingEntity user) {
    }

    public void appendTooltip(@Nullable World world, List<Text> tooltip, TooltipContext context) {
    }

    public static final Codec<Focus> CODEC = WizcraftRegistries.FOCUS_TYPE.getCodec().dispatch(Focus::getType, FocusType::customDataCodec);

    public static Focus fromNbt(NbtCompound nbt, Focus fallback) {
        return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(fallback);
    }

    public final NbtElement toNbt() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseGet(NbtCompound::new);
    }

    @Nullable
    protected String translationKey;

    public String getTranslationKey() {
        if (translationKey == null) {
            translationKey = Util.createTranslationKey("focus", getId());
        }

        return translationKey;
    }

    public Text getName() {
        return Text.translatable(getTranslationKey());
    }

    public final List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        List<Text> tooltip = new ArrayList<>();
        appendTooltip(player == null ? null : player.getWorld(), tooltip, context);
        return tooltip;
    }
}
