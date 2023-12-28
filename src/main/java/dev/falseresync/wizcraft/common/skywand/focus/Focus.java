package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.common.WizRegistries;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.lib.HasId;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public abstract class Focus implements HasId {
    public static final String STACK_NBT_KEY = "Focus";
    public static final Codec<Focus> CODEC;

    static {
        CODEC = WizRegistries.FOCUSES.getCodec().dispatch(Focus::getType, Focus::getCodec);
    }

    public abstract Codec<? extends Focus> getCodec();

    /**
     * @implNote This must return the same *static* instance, that's been registered. It's a tradeoff for not having a FocusType class
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

    public ActionResult use(World world, SkyWand wand, LivingEntity user) {
        return ActionResult.PASS;
    }

    public void tick(World world, SkyWand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void interrupt(World world, SkyWand wand, LivingEntity user, int remainingUseTicks) {
    }

    public void finish(World world, SkyWand wand, LivingEntity user) {
    }
}
