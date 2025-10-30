package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.ClientPlayerInventoryEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private int carriedIndex;

    @Inject(method = "ensureHasSentCarriedItem", at = @At("HEAD"))
    private void wizcraft$syncSelectedSlot(CallbackInfo ci) {
        if (minecraft.player == null) {
            return;
        }
        if (carriedIndex != minecraft.player.getInventory().selected) {
            ClientPlayerInventoryEvents.SELECTED_SLOT_CHANGED.invoker().onChanged(minecraft.player.getInventory(), carriedIndex);
        }
    }
}
