package dev.falseresync.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.common.WizRegistries;
import dev.falseresync.common.skywand.SkyWand;
import dev.falseresync.lib.HasId;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public abstract class Focus implements HasId {
    public static final Codec<Focus> CODEC;

    static {
        CODEC = WizRegistries.FOCUSES.getCodec().dispatch(Focus::getType, Focus::getCodec);
    }

    public abstract Codec<? extends Focus> getCodec();

    /**
     * @implNote This must return the same *static* instance, that's been registered. It's a tradeoff for not having a FocusType class
     */
    public abstract Focus getType();

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
