package dev.falseresync.wizcraft.common.skywand.focus;

import com.mojang.serialization.Codec;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.CommonReports;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.common.WizUtils;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class LightningFocus extends Focus {
    public static final int DEFAULT_COST = 10;
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

        if (user instanceof PlayerEntity player) {
            if (!wand.tryExpendCharge(DEFAULT_COST, player)) {
                CommonReports.insufficientCharge(world, user);
                return ActionResult.FAIL;
            }

            if (world instanceof ServerWorld serverWorld) {
                var lightning = EntityType.LIGHTNING_BOLT.create(world);
                var maxDistance = MathHelper.clamp(WizUtils.findViewDistance(world) * 16 / 4F, 32, 128);
                var raycastResult = user.raycast(maxDistance, 0, true);
                var pos = raycastResult.getType() == HitResult.Type.MISS
                        ? findGroundPos(serverWorld, raycastResult.getPos())
                        : raycastResult.getPos();
                // There won't be an NPE, because lightnings are not optional features. Hopefully.
                //noinspection DataFlowIssue
                lightning.refreshPositionAfterTeleport(pos);
                lightning.setChanneler(user instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
                world.spawnEntity(lightning);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    protected Vec3d findGroundPos(ServerWorld world, Vec3d posInAir) {
        return new Vec3d(
                posInAir.x,
                world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) posInAir.x, (int) posInAir.z),
                posInAir.z);
    }
}
