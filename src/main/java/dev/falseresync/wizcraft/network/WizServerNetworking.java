package dev.falseresync.wizcraft.network;

import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import dev.falseresync.wizcraft.network.c2s.UpdateSkyWandC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.network.ServerPlayerEntity;

public class WizServerNetworking {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(UpdateSkyWandC2SPacket.TYPE, WizServerNetworking::updateSkyWand);
    }

    public static void updateSkyWand(UpdateSkyWandC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        var inventory = player.getInventory();
        var mainHandStack = inventory.getMainHandStack();
        if (!mainHandStack.isOf(WizItems.SKY_WAND)) {
            throw new IllegalStateException("Must not update sky wand if it's not in the main hand");
        }

        var wand = SkyWand.fromStack(mainHandStack);
        var storage = PlayerInventoryStorage.of(inventory);

        // Hopefully this *should* be guaranteed to be a FocusItem
        // Otherwise liquid shit's hitting the fan
        var pickedFocus = ((FocusItem) packet.pickedFocus().getItem()).getFocus(packet.pickedFocus().toStack());
        var activeFocus = wand.getFocus();

        var tx = Transaction.openOuter();
        tx.addOuterCloseCallback(result -> {
            if (result.wasCommitted()) {
                wand.switchFocus(pickedFocus);
                wand.saveToStack(mainHandStack);
            }
        });
        try (tx) {
            var extracted = pickedFocus.getType() == WizFocuses.CHARGING
                    || storage.extract(packet.pickedFocus(), 1, tx) == 1;

            if (!extracted) {
                var storedFocusIgnoreNbt = StorageUtil.findStoredResource(storage, variant -> variant.isOf(packet.pickedFocus().getItem()));
                if (storedFocusIgnoreNbt != null) {
                    extracted = storage.extract(storedFocusIgnoreNbt, 1, tx) == 1;
                }
            }

            if (extracted) {
                var inserted = activeFocus.getType() == WizFocuses.CHARGING
                        || storage.insert(ItemVariant.of(activeFocus.toStack()), 1, tx) == 1;
                if (inserted) {
                    tx.commit();
                }
            }
        }
    }
}
