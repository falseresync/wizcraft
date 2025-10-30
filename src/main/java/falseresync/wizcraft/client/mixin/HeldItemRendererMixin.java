package falseresync.wizcraft.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import falseresync.wizcraft.client.render.entity.EnergyVeilFeatureRenderer;
import falseresync.wizcraft.client.render.item.ChargingFocusUseAction;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {
    private @Shadow @Final EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderArmWithItem", at = @At("TAIL"))
    public void wizcraft$renderFirstPersonItem$energyVeil(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        var renderer = (PlayerRenderer) entityRenderDispatcher.getRenderer(player);
        var animationProgress = renderer.getBob(player, tickDelta);
        ((EnergyVeilFeatureRenderer.Accessor) renderer)
                .wizcraft$getEnergyVeilRenderer()
                .renderInFirstPerson(matrices, vertexConsumers, light, player, tickDelta, animationProgress);
    }

    @WrapOperation(
            method = "renderArmWithItem",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;")),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V",
                    ordinal = 0))
    public void wizcraft$renderFirstPersonItem$customUseAction(ItemInHandRenderer instance,
                                                               PoseStack matrices,
                                                               HumanoidArm arm,
                                                               float equipProgress,
                                                               Operation<Void> original,
                                                               @Local(argsOnly = true) AbstractClientPlayer player,
                                                               @Local(argsOnly = true, ordinal = 0) float tickDelta,
                                                               @Local(argsOnly = true, ordinal = 1) float pitch,
                                                               @Local(argsOnly = true) InteractionHand hand,
                                                               @Local(argsOnly = true, ordinal = 2) float swingProgress,
                                                               @Local(argsOnly = true) ItemStack stack,
                                                               @Local(argsOnly = true) MultiBufferSource vertexConsumers,
                                                               @Local(argsOnly = true) int light) {
        if (stack.is(WizcraftItems.WAND) && WizcraftItems.WAND.getEquipped(stack).is(WizcraftItems.CHARGING_FOCUS)) {
            ChargingFocusUseAction.applyFirstPersonTransformation(new ChargingFocusUseAction.HeldItemRendererContext(
                    player, stack, hand, arm, pitch, swingProgress, equipProgress, matrices, vertexConsumers, tickDelta, light
            ));
        } else {
            original.call(instance, matrices, arm, equipProgress);
        }
    }
}
