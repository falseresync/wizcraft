package falseresync.wizcraft.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import falseresync.wizcraft.client.render.item.ChargingFocusUseAction;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidModel.class)
public class BipedEntityModelMixin {
    @WrapOperation(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/HumanoidModel;poseRightArm(Lnet/minecraft/world/entity/LivingEntity;)V"))
    public void wizcraft$positionRightArm(HumanoidModel<LivingEntity> instance,
                                          LivingEntity entity,
                                          Operation<Void> original,
                                          @Local(argsOnly = true, ordinal = 0) float limbAngle,
                                          @Local(argsOnly = true, ordinal = 1) float limbDistance,
                                          @Local(argsOnly = true, ordinal = 2) float animationProgress,
                                          @Local(argsOnly = true, ordinal = 3) float headYaw,
                                          @Local(argsOnly = true, ordinal = 4) float headPitch) {
        if (entity.isUsingItem()) {
            var context = new ChargingFocusUseAction.BipedEntityModelContext(
                    instance, entity, HumanoidArm.RIGHT, limbAngle, limbDistance, animationProgress, headYaw, headPitch
            );

            if (!ChargingFocusUseAction.shouldSkipDefaultArmPositioning(context)) {
                original.call(instance, entity);
            }

            var stack = entity.getMainHandItem();
            if (stack.is(WizcraftItems.WAND) && WizcraftItems.WAND.getEquipped(stack).is(WizcraftItems.CHARGING_FOCUS)) {
                ChargingFocusUseAction.positionArm(context);
            }
        } else {
            original.call(instance, entity);
        }
    }
}
