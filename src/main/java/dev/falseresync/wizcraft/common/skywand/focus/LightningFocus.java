package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.lib.WizUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class LightningFocus extends Focus {
    public static final Codec<LightningFocus> CODEC = Codec.unit(() -> WizFocuses.LIGHTNING);
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "lightning");

    @Override
    public Codec<LightningFocus> getCodec() {
        return CODEC;
    }

    @Override
    public LightningFocus getType() {
        return WizFocuses.LIGHTNING;
    }

    @Override
    public Item getItem() {
        return WizItems.LIGHTNING_FOCUS;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ActionResult use(World world, SkyWand wand, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " attempts to use a lightning focus");
        var charge = wand.getCharge();
        var cost = user instanceof PlayerEntity player && player.isCreative() ? 0 : 25;
        if (charge >= cost) {
            wand.expendCharge(cost);
            if (world instanceof ServerWorld serverWorld) {
                var lightning = EntityType.LIGHTNING_BOLT.create(world);
                var raycastResult = user.raycast(WizUtils.findViewDistance(world) * 16 / 2F, 0, true);
                var pos = raycastResult.getType() == HitResult.Type.MISS
                        ? findGroundPos(serverWorld, raycastResult.getPos())
                        : raycastResult.getPos();
                // There won't be an NPE, because lightnings are not optional features. Hopefully.
                //noinspection DataFlowIssue
                lightning.refreshPositionAfterTeleport(pos);
                lightning.setChanneler(user instanceof ServerPlayerEntity player ? player : null);
                world.spawnEntity(lightning);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    protected Vec3d findGroundPos(ServerWorld world, Vec3d posInAir) {
        return new Vec3d(
                posInAir.x,
                world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) posInAir.x, (int) posInAir.z),
                posInAir.z);
    }
}
