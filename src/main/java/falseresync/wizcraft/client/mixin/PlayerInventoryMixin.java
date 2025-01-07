package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.ClientPlayerInventoryEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(method = "markDirty", at = @At("HEAD"))
    private void wizcraft$markDirty(CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayerInventoryEvents.CHANGED.invoker().onChanged((PlayerInventory) (Object) this);
        }
    }
}
