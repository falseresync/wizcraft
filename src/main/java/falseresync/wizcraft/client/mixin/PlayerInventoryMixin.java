package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.*;
import net.minecraft.entity.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(method = "markDirty", at = @At("HEAD"))
    private void wizcraft$markDirty(CallbackInfo ci) {
        ClientPlayerInventoryEvents.CONTENTS_CHANGED.invoker().onChanged((PlayerInventory) (Object) this);
    }
}
