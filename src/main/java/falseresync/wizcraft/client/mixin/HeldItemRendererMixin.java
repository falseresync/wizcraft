package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.render.entity.EnergyVeilFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
    public void wizcraft$renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var renderer = (PlayerEntityRenderer) entityRenderDispatcher.getRenderer(player);
        var animationProgress = renderer.getAnimationProgress(player, tickDelta);
        ((EnergyVeilFeatureRenderer.Accessor) renderer)
                .wizcraft$getEnergyVeilRenderer()
                .renderInFirstPerson(matrices, vertexConsumers, light, player, tickDelta, animationProgress);
    }
}
