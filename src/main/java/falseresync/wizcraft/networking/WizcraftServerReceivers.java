package falseresync.wizcraft.networking;

import falseresync.wizcraft.common.item.*;
import falseresync.wizcraft.networking.c2s.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.item.*;

public class WizcraftServerReceivers {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ChangeWandFocusC2SPayload.ID, WizcraftServerReceivers::changeWandFocus);
    }

    private static void changeWandFocus(ChangeWandFocusC2SPayload payload, ServerPlayNetworking.Context context) {
        var player = context.player();
        var wandStack = player.getMainHandStack();
        if (!wandStack.isIn(WizcraftItemTags.WANDS)) {
            return;
        }

        if (payload.slot() < 0) {
            return;
        }

        switch (payload.destination()) {
            case PLAYER_INVENTORY -> {
                if (payload.slot() < player.getInventory().size() - 1) {
                    var newFocusStack = player.getInventory().getStack(payload.slot());
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, newFocusStack, player);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().setStack(payload.slot(), exchange.getValue());
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
                    if (payload.slot() > inventoryComponent.size() - 1) {
                        return;
                    }

                    var picked = inventoryComponent.stacks().get(payload.slot());
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, picked, player);
                    if (exchange.getResult().isAccepted()) {
                        var inventory = inventoryComponent.toModifiable();
                        inventory.setStack(payload.slot(), exchange.getValue());
                        inventory.flush(beltStack);
                    }
                });
            }
        }
    }
}
