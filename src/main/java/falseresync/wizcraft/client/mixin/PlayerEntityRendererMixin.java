package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.render.entity.EnergyVeilFeatureRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> implements EnergyVeilFeatureRenderer.Accessor {
    @Mutable
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayer> energyVeilFeatureRenderer;

    public PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void wizcraft$init(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        energyVeilFeatureRenderer = new EnergyVeilFeatureRenderer<>(this, ctx.getModelSet());
        addLayer(energyVeilFeatureRenderer);
    }

    @Override
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayer> wizcraft$getEnergyVeilRenderer() {
        return energyVeilFeatureRenderer;
    }
}
