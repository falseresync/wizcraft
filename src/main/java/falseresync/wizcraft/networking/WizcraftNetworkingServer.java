package falseresync.wizcraft.networking;


import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, newFocusStack, player);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().setStack(packet.slot(), exchange.getValue());
                    }
                } else {
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, ItemStack.EMPTY, player);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().offerOrDrop(exchange.getValue());
                    }
                }
            }
            case WAND_INVENTORY -> {
                // do nothing, yet
            }
            case FOCUSES_BELT -> {
                WizcraftItems.FOCUSES_BELT.findTrinketStack(player).ifPresent(beltStack -> {
                    var inventoryComponent = WizcraftItems.FOCUSES_BELT.getOrCreateInventoryComponent(beltStack);
                    if (packet.slot() > inventoryComponent.size() - 1) {
                        return;
                    }

                    var picked = inventoryComponent.stacks().get(packet.slot());
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, picked, player);
                    if (exchange.getResult().isAccepted()) {
                        var inventory = inventoryComponent.toModifiable();
                        inventory.setStack(packet.slot(), exchange.getValue());
                        inventory.flush(beltStack);
                    }
                });
            }
        }
    }
}
