package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.render.entity.EnergyVeilFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntityRenderer.class, priority = 2000)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements EnergyVeilFeatureRenderer.Accessor {
    @Mutable
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> energyVeilFeatureRenderer;

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void wizcraft$init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        energyVeilFeatureRenderer = new EnergyVeilFeatureRenderer<>(this, ctx.getModelLoader());
        features.addFirst(energyVeilFeatureRenderer);
    }

    @Override
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> wizcraft$getEnergyVeilRenderer() {
        return energyVeilFeatureRenderer;
    }
}
