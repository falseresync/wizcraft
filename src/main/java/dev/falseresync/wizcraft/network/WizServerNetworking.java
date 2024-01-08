package dev.falseresync.wizcraft.network;

import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusStack;
import dev.falseresync.wizcraft.common.wand.focus.WizFocuses;
import dev.falseresync.wizcraft.network.c2s.UpdateSkyWandFocusC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.network.ServerPlayerEntity;

public class WizServerNetworking {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(UpdateSkyWandFocusC2SPacket.TYPE, WizServerNetworking::updateSkyWandFocus);
    }

    private static void updateSkyWandFocus(UpdateSkyWandFocusC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        var inventory = player.getInventory();
        var mainHandStack = inventory.getMainHandStack();
        if (!mainHandStack.isOf(WizItems.SKY_WAND)) {
            throw new IllegalStateException("Must not update sky wand if it's not in the main hand");
        }

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
                wand.attach(mainHandStack);
            }
        });
        try (tx) {
            var extracted = pickedFocusStack.getFocus().getType() == WizFocuses.CHARGING
                    || storage.extract(packet.pickedFocus(), 1, tx) == 1;

            if (!extracted) {
                var storedFocusIgnoreNbt = StorageUtil.findStoredResource(storage, variant -> variant.isOf(packet.pickedFocus().getItem()));
                if (storedFocusIgnoreNbt != null) {
                    extracted = storage.extract(storedFocusIgnoreNbt, 1, tx) == 1;
                }
            }

            if (extracted) {
                var inserted = activeFocusStack.getFocus().getType() == WizFocuses.CHARGING
                        || storage.insert(activeFocusStack.toItemVariant(), 1, tx) == 1;
                if (inserted) {
                    tx.commit();
                }
            }
        }
    }
}
