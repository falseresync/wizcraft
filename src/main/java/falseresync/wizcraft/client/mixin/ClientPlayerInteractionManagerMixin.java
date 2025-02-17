package falseresync.wizcraft.client.mixin;

import falseresync.wizcraft.client.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private int lastSelectedSlot;

    @Inject(method = "syncSelectedSlot", at = @At("HEAD"))
    private void wizcraft$syncSelectedSlot(CallbackInfo ci) {
        if (client.player == null) {
            return;
        }
        if (lastSelectedSlot != client.player.getInventory().selectedSlot) {
            ClientPlayerInventoryEvents.SELECTED_SLOT_CHANGED.invoker().onChanged(client.player.getInventory(), lastSelectedSlot);
        }
    }
}
