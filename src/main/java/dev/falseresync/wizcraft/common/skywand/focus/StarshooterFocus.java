package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.entity.StarProjectileEntity;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StarshooterFocus extends Focus {
    public static final Codec<StarshooterFocus> CODEC = Codec.unit(() -> WizFocuses.STARSHOOTER);
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "starshooter");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Codec<StarshooterFocus> getCodec() {
        return CODEC;
    }

    @Override
    public StarshooterFocus getType() {
        return WizFocuses.STARSHOOTER;
    }

    @Override
    public ActionResult use(World world, SkyWand wand, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " attempts to use a starshooter focus");
        var charge = wand.getCharge();
        var cost = user instanceof PlayerEntity player && player.isCreative() ? 0 : 10;
        if (charge >= cost) {
            wand.expendCharge(cost);
            var projectile = new StarProjectileEntity(user, world);
            world.spawnEntity(projectile);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}
