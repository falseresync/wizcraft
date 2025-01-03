package dev.falseresync.wizcraft.network;

import com.google.common.base.Preconditions;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusStack;
import dev.falseresync.wizcraft.common.wand.focus.WizcraftFocuses;
import dev.falseresync.wizcraft.network.c2s.UpdateWandFocusC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class WizcraftServerNetworking {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(UpdateWandFocusC2SPacket.ID, WizcraftServerNetworking::updateWandFocus);
    }

    private static void updateWandFocus(UpdateWandFocusC2SPacket packet, ServerPlayNetworking.Context context) {
        var inventory = context.player().getInventory();
        var mainHandStack = inventory.getMainHandStack();
        Preconditions.checkState(mainHandStack.isOf(WizcraftItems.WAND), "Must not update wand if it's not in the main hand");

        var wand = Wand.fromStack(mainHandStack);
        var storage = PlayerInventoryStorage.of(inventory);

        // Hopefully this *should* be guaranteed to be a FocusItem
        // Otherwise liquid shit's hitting the fan
        var pickedFocusStack = new FocusStack(packet.pickedFocus());
        var activeFocusStack = wand.getFocusStack();

        var tx = Transaction.openOuter();
        tx.addOuterCloseCallback(result -> {
            if (result.wasCommitted()) {
                wand.switchFocus(pickedFocusStack);
                wand.attachTo(mainHandStack);
            }
        });
        try (tx) {
            var extracted = pickedFocusStack.getFocus().getType() == WizcraftFocuses.CHARGING
                    || storage.extract(packet.pickedFocus(), 1, tx) == 1;

            if (!extracted) {
                var storedFocusIgnoreNbt = StorageUtil.findStoredResource(storage, variant -> variant.isOf(packet.pickedFocus().getItem()));
                if (storedFocusIgnoreNbt != null) {
                    extracted = storage.extract(storedFocusIgnoreNbt, 1, tx) == 1;
                }
            }

            if (extracted) {
                var inserted = activeFocusStack.getFocus().getType() == WizcraftFocuses.CHARGING
                        || storage.insert(activeFocusStack.toItemVariant(), 1, tx) == 1;
                if (inserted) {
                    tx.commit();
                }
            }
        }
    }
}
