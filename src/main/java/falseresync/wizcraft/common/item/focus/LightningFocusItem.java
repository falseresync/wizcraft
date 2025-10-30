package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftUtil;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LightningFocusItem extends FocusItem {
    public LightningFocusItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer player) {
            if (Wizcraft.getChargeManager().tryExpendWandCharge(gadgetStack, 10, user)) {
                var lightning = EntityType.LIGHTNING_BOLT.create(world);
                var maxDistance = Mth.clamp(WizcraftUtil.findViewDistance(world) * 16 / 4F, 32, 128);
                var raycastResult = user.pick(maxDistance, 0, true);
                var pos = raycastResult.getType() == HitResult.Type.MISS
                        ? findGroundPos((ServerLevel) world, raycastResult.getLocation())
                        : raycastResult.getLocation();
                // There won't be an NPE, because lightnings are not optional features. Hopefully.
                //noinspection DataFlowIssue
                lightning.moveTo(pos);
                lightning.setCause(player);
                lightning.setAttached(WizcraftAttachments.THUNDERLESS_LIGHTNING, true);
                world.addFreshEntity(lightning);
                focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
                return InteractionResultHolder.success(gadgetStack);
            }

            Reports.insufficientCharge(player);
            return InteractionResultHolder.fail(gadgetStack);
        }

        return InteractionResultHolder.consume(gadgetStack);
    }

    protected Vec3 findGroundPos(ServerLevel world, Vec3 posInAir) {
        return new Vec3(
                posInAir.x,
                world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) posInAir.x, (int) posInAir.z),
                posInAir.z);
    }
}
