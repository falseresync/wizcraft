package falseresync.wizcraft.networking;

import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;

public class WizcraftServerReceivers {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ChangeWandFocusC2SPayload.ID, WizcraftServerReceivers::changeWandFocus);
    }

    private static void changeWandFocus(ChangeWandFocusC2SPayload payload, ServerPlayNetworking.Context context) {
        var player = context.player();
        var wandStack = player.getMainHandItem();
        if (!wandStack.is(WizcraftItemTags.WANDS)) {
            return;
        }

        if (payload.slot() < 0) {
            return;
        }

        switch (payload.destination()) {
            case PLAYER_INVENTORY -> {
                if (payload.slot() < player.getInventory().getContainerSize() - 1) {
                    var newFocusStack = player.getInventory().getItem(payload.slot());
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, newFocusStack, player);
                    if (exchange.getResult().consumesAction()) {
                        player.getInventory().setItem(payload.slot(), exchange.getObject());
                    }
                } else {
                    var exchange = WizcraftItems.WAND.exchangeFocuses(wandStack, ItemStack.EMPTY, player);
                    if (exchange.getResult().consumesAction()) {
                        player.getInventory().placeItemBackInInventory(exchange.getObject());
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
                    if (exchange.getResult().consumesAction()) {
                        var inventory = inventoryComponent.toModifiable();
                        inventory.setItem(payload.slot(), exchange.getObject());
                        inventory.flush(beltStack);
                    }
                });
            }
        }
    }
}
