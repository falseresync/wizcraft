package falseresync.wizcraft.networking;


import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class WizcraftNetworkingServer {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(ChangeWandFocusC2SPacket.ID, WizcraftNetworkingServer::changeWandFocus);
    }

    private static void changeWandFocus(ChangeWandFocusC2SPacket packet, ServerPlayNetworking.Context context) {
        var player = context.player();
        var wandStack = player.getMainHandStack();
        if (!wandStack.isIn(WizcraftItemTags.WANDS)) {
            return;
        }

        var newFocusStack = player.getInventory().getStack(packet.slot());
        var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, newFocusStack);
        if (exchange.getResult().isAccepted()) {
            player.getInventory().setStack(packet.slot(), exchange.getValue());
        }
    }
}
