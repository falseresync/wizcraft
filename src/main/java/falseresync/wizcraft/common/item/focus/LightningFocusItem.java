package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.*;
import falseresync.wizcraft.networking.report.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class LightningFocusItem extends FocusItem {
    public LightningFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, 10, user)) {
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

            WizcraftReports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
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
