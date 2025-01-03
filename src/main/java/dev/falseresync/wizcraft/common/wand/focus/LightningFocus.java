package dev.falseresync.wizcraft.common.wand.focus;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.wand.focus.Focus;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusType;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.common.report.WizcraftReports;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.common.CommonUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class LightningFocus extends Focus {
    public static final int DEFAULT_COST = 10;
    public static final Identifier ID = wid("lightning");

    @Override
    public FocusType<LightningFocus> getType() {
        return WizcraftFocuses.LIGHTNING;
    }

    @Override
    public Item getItem() {
        return WizcraftItems.LIGHTNING_FOCUS;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ActionResult use(World world, Wand wand, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            if (!wand.tryExpendCharge(DEFAULT_COST, player)) {
                Report.trigger(player, WizcraftReports.Wand.INSUFFICIENT_CHARGE);
                return ActionResult.FAIL;
            }

            var lightning = EntityType.LIGHTNING_BOLT.create(world);
            var maxDistance = MathHelper.clamp(CommonUtils.findViewDistance(world) * 16 / 4F, 32, 128);
            var raycastResult = user.raycast(maxDistance, 0, true);
            var pos = raycastResult.getType() == HitResult.Type.MISS
                    ? findGroundPos((ServerWorld) world, raycastResult.getPos())
                    : raycastResult.getPos();
            // There won't be an NPE, because lightnings are not optional features. Hopefully.
            //noinspection DataFlowIssue
            lightning.refreshPositionAfterTeleport(pos);
            lightning.setChanneler(player);
            ((WizcraftLightning) lightning).wizcraft$setThunderless();
            world.spawnEntity(lightning);

            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    protected Vec3d findGroundPos(ServerWorld world, Vec3d posInAir) {
        return new Vec3d(
                posInAir.x,
                world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) posInAir.x, (int) posInAir.z),
                posInAir.z);
    }

    public interface WizcraftLightning {
        void wizcraft$setThunderless();
    }
}
