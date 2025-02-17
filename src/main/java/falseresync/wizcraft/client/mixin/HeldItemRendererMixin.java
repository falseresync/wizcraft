package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.render.entity.*;
import net.minecraft.client.network.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    private @Shadow @Final EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
    public void wizcraft$renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var renderer = (PlayerEntityRenderer) entityRenderDispatcher.getRenderer(player);
        var animationProgress = renderer.getAnimationProgress(player, tickDelta);
        ((EnergyVeilFeatureRenderer.Accessor) renderer)
                .wizcraft$getEnergyVeilRenderer()
                .renderInFirstPerson(matrices, vertexConsumers, light, player, tickDelta, animationProgress);
    }
}
