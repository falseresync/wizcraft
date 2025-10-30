package falseresync.wizcraft.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ChargingFocusUseAction {
    public static void applyFirstPersonTransformation(HeldItemRendererContext context) {
        int i = context.arm == HumanoidArm.RIGHT ? 1 : -1;
        context.matrices.translate((float) i * 0.56F, -0.72F + context.equipProgress * -0.6F, -0.72F);
    }

    public static void positionArm(BipedEntityModelContext context) {
        context.model.rightArm.xRot = context.model.rightArm.xRot * 0.5F - (float) Math.PI;
        context.model.rightArm.yRot = 0.0F;
    }

    public static boolean shouldSkipDefaultArmPositioning(BipedEntityModelContext context) {
        return false;
    }

    public static boolean shouldNeverSwingArm(BipedEntityModelContext context) {
        return false;
    }

    public static boolean isTwoHanded(BipedEntityModelContext context) {
        return false;
    }

    public record HeldItemRendererContext(
            AbstractClientPlayer player,
            ItemStack stack,
            InteractionHand hand,
            HumanoidArm arm,
            float pitch,
            float swingProgress,
            float equipProgress,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            float tickDelta,
            int light
    ) {
    }

    public record BipedEntityModelContext(
            HumanoidModel<LivingEntity> model, LivingEntity entity, HumanoidArm arm, float limbAngle,
            float limbDistance, float animationProgress, float headYaw, float headPitch
    ) {
    }
}
