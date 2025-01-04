package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.networking.report.Report;
import falseresync.wizcraft.networking.report.WizcraftReports;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class LightningFocusItem extends FocusItem {
    public LightningFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (WizcraftItems.WAND.tryExpendCharge(wandStack, 10)) {
                var lightning = EntityType.LIGHTNING_BOLT.create(world);
                var maxDistance = MathHelper.clamp(Wizcraft.findViewDistance(world) * 16 / 4F, 32, 128);
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

                return TypedActionResult.success(wandStack);
            }

            Report.trigger(player, WizcraftReports.WAND_INSUFFICIENT_CHARGE);
            return TypedActionResult.fail(wandStack);
        }

        return TypedActionResult.consume(wandStack);
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
