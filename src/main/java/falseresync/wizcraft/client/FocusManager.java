package falseresync.wizcraft.client;

import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class FocusManager {
    public FocusManager() {
        ClientPlayerInventoryEvents.CHANGED.register(inventory -> {
            manage(inventory, false);
        });
    }

    public void onKeyPressed(MinecraftClient client, ClientPlayerEntity player) {
        manage(player.getInventory(), true);
    }

    private void manage(PlayerInventory inventory, boolean shouldPickNext) {
        var wandStack = inventory.getMainHandStack();
        if (!wandStack.isIn(WizcraftItemTags.WANDS)) {
            WizcraftClient.getHud().getFocusPicker().hide();
            return;
        }

        var equippedFocusStack = wandStack.getOrDefault(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        var focusStacks = inventory.main.stream()
                .filter(it -> it.isIn(WizcraftItemTags.FOCUSES))
                .collect(Collectors.toCollection(LinkedList::new));
        if (!equippedFocusStack.isEmpty()) {
            focusStacks.addFirst(equippedFocusStack);
        }

        if (focusStacks.isEmpty()) {
            WizcraftClient.getHud().getMessageDisplay().postImportant(Text.translatable("hud.wizcraft.wand.no_focuses"));
            return;
        }
        var focusPicker = WizcraftClient.getHud().getFocusPicker();
        focusPicker.upload(focusStacks);

        if (shouldPickNext) {
            // First press opens the menu, following ones change the focus
            // But if there was no focus equipped, pick on first press anyway
            if (focusPicker.isVisible() || equippedFocusStack.isEmpty()) {
                focusPicker.pickNext();
            }
            focusPicker.show();

            var currentlyPickedFocusStack = focusPicker.getCurrentlyPicked();
            if (ItemStack.areItemsAndComponentsEqual(equippedFocusStack, currentlyPickedFocusStack)) {
                return;
            }

            var slot = inventory.getSlotWithStack(currentlyPickedFocusStack);
            ClientPlayNetworking.send(new ChangeWandFocusC2SPacket(slot));
        }
    }
}
