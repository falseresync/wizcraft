package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.ClientPlayerInventoryEvents;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(method = "markDirty", at = @At("HEAD"))
    private void wizcraft$markDirty(CallbackInfo ci) {
        ClientPlayerInventoryEvents.CONTENTS_CHANGED.invoker().onChanged((PlayerInventory) (Object) this);
    }
}
