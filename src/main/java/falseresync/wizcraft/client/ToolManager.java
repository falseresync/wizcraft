package falseresync.wizcraft.client;

import dev.emi.trinkets.api.event.TrinketDropCallback;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import falseresync.wizcraft.client.hud.FocusPickerHudItem;
import falseresync.wizcraft.client.hud.ChargeDisplayHudItem;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPacket;
import falseresync.wizcraft.networking.c2s.WandFocusDestination;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class ToolManager {
    private final ChargeDisplayHudItem chargeDisplay;
    private final FocusPickerHudItem focusPicker;

    public ToolManager() {
        chargeDisplay = WizcraftClient.getHud().getChargeDisplay();
        focusPicker = WizcraftClient.getHud().getFocusPicker();

        TrinketEquipCallback.EVENT.register((stack, slot, entity) -> {
            if (entity instanceof PlayerEntity player) {
                var wandStack = scanInventoryForWands(player.getInventory());
                if (wandStack != null) {
                    setupChargeDisplay(player, wandStack);
                }
            }
        });

        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (entity instanceof PlayerEntity player) {
                hideChargeDisplay(player);
            }
            return rule;
        });

        TrinketUnequipCallback.EVENT.register((stack, slot, entity) -> {
            if (entity instanceof PlayerEntity player) {
                hideChargeDisplay(player);
            }
        });

        ClientPlayerInventoryEvents.SELECTED_SLOT_CHANGED.register((inventory, lastSelectedSlot) -> {
            var wandStack = scanInventoryForWands(inventory);
            if (wandStack != null) {
                setupChargeDisplay(inventory.player, wandStack);
            } else {
                chargeDisplay.hide();
                focusPicker.hide();
            }
        });

        ClientPlayerInventoryEvents.CONTENTS_CHANGED.register(inventory -> {
            var wandStack = scanInventoryForWands(inventory);
            if (wandStack != null) {
                setupChargeDisplay(inventory.player, wandStack);
                scanInventoryAndSetupFocusPicker(inventory, wandStack, false);
            } else {
                chargeDisplay.hide();
                focusPicker.hide();
            }
        });
    }

    public void onKeyPressed(MinecraftClient client, ClientPlayerEntity player) {
        var wandStack = scanInventoryForWands(player.getInventory());
        if (wandStack == null) {
            focusPicker.hide();
            return;
        }
        scanInventoryAndSetupFocusPicker(player.getInventory(), wandStack, true);
    }

    @Nullable
    private ItemStack scanInventoryForWands(PlayerInventory inventory) {
        var wandStack = inventory.getMainHandStack();
        return wandStack.isIn(WizcraftItemTags.WANDS) ? wandStack : null;
    }

    private void setupChargeDisplay(PlayerEntity player, ItemStack wandStack) {
        if (player.hasAttached(WizcraftDataAttachments.HAS_TRUESEER_GOGGLES)) {
            chargeDisplay.upload(wandStack);
            chargeDisplay.show();
        }
    }

    private void hideChargeDisplay(PlayerEntity player) {
        if (chargeDisplay.isVisible() && !player.hasAttached(WizcraftDataAttachments.HAS_TRUESEER_GOGGLES)) {
            chargeDisplay.hide();
        }
    }

    private void scanInventoryAndSetupFocusPicker(PlayerInventory inventory, ItemStack wandStack, boolean shouldPickNext) {
        var equipped = wandStack.getOrDefault(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        var belt = WizcraftItems.FOCUSES_BELT.findTrinketStack(inventory.player);
        var focusStacks = belt.isPresent()
                ? belt.stream()
                        .map(WizcraftItems.FOCUSES_BELT::getOrCreateInventoryComponent)
                        .flatMap(inventoryComponent -> inventoryComponent.stacks().stream())
                        .filter(stack -> !stack.isEmpty())
                        .collect(Collectors.toCollection(LinkedList::new))
                : inventory.main.stream()
                        .filter(it -> it.isIn(WizcraftItemTags.FOCUSES))
                        .collect(Collectors.toCollection(LinkedList::new));

        if (!equipped.isEmpty()) {
            focusStacks.addFirst(equipped);
        }

        if (focusStacks.isEmpty()) {
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.translatable("hud.wizcraft.wand.no_focuses"), false);
            return;
        }

        var destination = WandFocusDestination.PLAYER_INVENTORY;
        if (belt.isPresent()) {
            destination = WandFocusDestination.FOCUSES_BELT;
        } // wand inventories go here

        var picked = setupFocusPicker(wandStack, focusStacks, equipped, shouldPickNext);
        if (picked != null) {
            sendChangeWandFocusPacket(inventory, destination, picked);
        }
    }

    @Nullable
    private ItemStack setupFocusPicker(ItemStack wandStack, LinkedList<ItemStack> focusStacks, ItemStack equipped, boolean shouldPickNext) {
        focusPicker.upload(wandStack, focusStacks);

        if (shouldPickNext) {
            // First press opens the menu, following ones change the focus
            // But if there was no focus equipped, pick on first press anyway
            if (focusPicker.isVisible() || equipped.isEmpty()) {
                focusPicker.pickNext();
            }
            focusPicker.show();

            var picked = focusPicker.getCurrentlyPicked();
            return ItemStack.areItemsAndComponentsEqual(equipped, picked) ? null : picked;
        }

        return null;
    }

    private void sendChangeWandFocusPacket(PlayerInventory inventory, WandFocusDestination destination, ItemStack picked) {
        if (destination == WandFocusDestination.PLAYER_INVENTORY) {
            var slot = inventory.getSlotWithStack(picked);
            ClientPlayNetworking.send(new ChangeWandFocusC2SPacket(destination, slot));
        } else if (destination == WandFocusDestination.FOCUSES_BELT) {
            var slot = WizcraftItems.FOCUSES_BELT.findTrinketStack(inventory.player)
                    .map(WizcraftItems.FOCUSES_BELT::getOrCreateInventoryComponent)
                    .map(component -> component.getSlotWithStack(picked))
                    .orElse(-1);
            ClientPlayNetworking.send(new ChangeWandFocusC2SPacket(destination, slot));
        }
    }
}
