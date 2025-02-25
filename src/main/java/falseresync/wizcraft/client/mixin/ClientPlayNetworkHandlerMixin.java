package falseresync.wizcraft.client.mixin;

import com.llamalad7.mixinextras.sugar.*;
import falseresync.wizcraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("TAIL"))
    private void wizcraft$onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci, @Local PlayerEntity player) {
        ClientPlayerInventoryEvents.CONTENTS_CHANGED.invoker().onChanged(player.getInventory());
    }
}
