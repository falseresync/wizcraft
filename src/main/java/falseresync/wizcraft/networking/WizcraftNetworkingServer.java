package falseresync.wizcraft.networking;


import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;

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

        if (packet.slot() < 0) {
            return;
        }

        switch (packet.destination()) {
            case PLAYER_INVENTORY -> {
                if (packet.slot() < player.getInventory().size() - 1) {
                    var newFocusStack = player.getInventory().getStack(packet.slot());
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, newFocusStack);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().setStack(packet.slot(), exchange.getValue());
                    }
                } else {
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, ItemStack.EMPTY);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().offerOrDrop(exchange.getValue());
                    }
                }
            }
            case WAND_INVENTORY -> {
                // do nothing, yet
            }
            case FOCUSES_BELT -> {
                WizcraftItems.FOCUSES_BELT.findTrinketContents(player).ifPresent(beltContents -> {
                    if (packet.slot() > beltContents.size() - 1) {
                        return;
                    }

                    var picked = beltContents.removeStack(packet.slot());
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, picked);
                    if (exchange.getResult().isAccepted()) {
                        beltContents.setStack(packet.slot(), exchange.getValue());
                    } else {
                        beltContents.setStack(packet.slot(), picked);
                    }
                });
            }
        }
    }
}
