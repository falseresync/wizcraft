package falseresync.wizcraft.client;

import dev.emi.trinkets.api.event.TrinketDropCallback;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import falseresync.wizcraft.client.hud.ChargeDisplayHudItem;
import falseresync.wizcraft.client.hud.FocusPickerHudItem;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPayload;
import falseresync.wizcraft.networking.c2s.WandFocusDestination;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class ToolManager {
    private final ChargeDisplayHudItem chargeDisplay;
    private final FocusPickerHudItem focusPicker;

    public ToolManager() {
        chargeDisplay = WizcraftClient.getHud().getChargeDisplay();
        focusPicker = WizcraftClient.getHud().getFocusPicker();

        TrinketEquipCallback.EVENT.register((stack, slot, entity) -> {
            if (entity instanceof Player player) {
                var wandStack = scanInventoryForWands(player.getInventory());
                if (wandStack != null) {
                    setupChargeDisplay(player, wandStack);
                }
            }
        });

        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (entity instanceof Player player) {
                hideChargeDisplayIfShould(player);
            }
            return rule;
        });

        TrinketUnequipCallback.EVENT.register((stack, slot, entity) -> {
            if (entity instanceof Player player) {
                hideChargeDisplayIfShould(player);
            }
        });

        ClientInventoryEvents.SELECTED_SLOT_CHANGED.register((inventory, lastSelectedSlot) -> {
            var wandStack = scanInventoryForWands(inventory);
            if (wandStack != null) {
                setupChargeDisplay(inventory.player, wandStack);
            } else {
                chargeDisplay.hide();
                focusPicker.hide();
            }
        });

        ClientInventoryEvents.CONTENTS_CHANGED.register(inventory -> {
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

    public void onKeyPressed(Minecraft client, LocalPlayer player) {
        var wandStack = scanInventoryForWands(player.getInventory());
        if (wandStack == null) {
            focusPicker.hide();
            return;
        }
        scanInventoryAndSetupFocusPicker(player.getInventory(), wandStack, true);
    }

    @Nullable
    private ItemStack scanInventoryForWands(Inventory inventory) {
        var wandStack = inventory.getSelected();
        return wandStack.is(WizcraftItemTags.WANDS) ? wandStack : null;
    }

    private void setupChargeDisplay(Player player, ItemStack wandStack) {
        if (player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)) {
            chargeDisplay.upload(wandStack);
            chargeDisplay.show();
        }
    }

    private void hideChargeDisplayIfShould(Player player) {
        if (chargeDisplay.isVisible() && !player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)) {
            chargeDisplay.hide();
        }
    }

    private void scanInventoryAndSetupFocusPicker(Inventory inventory, ItemStack wandStack, boolean shouldPickNext) {
        var equipped = wandStack.getOrDefault(WizcraftComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        var belt = WizcraftItems.FOCUSES_BELT.findTrinketStack(inventory.player);
        var focusStacks = belt
                .map(it -> WizcraftItems.FOCUSES_BELT.getOrCreateInventoryComponent(it).stacks().stream().filter(stack -> !stack.isEmpty()))
                .orElseGet(() -> inventory.items.stream().filter(it -> it.is(WizcraftItemTags.FOCUSES)))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!equipped.isEmpty()) {
            focusStacks.addFirst(equipped);
        }

        if (focusStacks.isEmpty()) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("hud.wizcraft.wand.no_focuses"), false);
            return;
        }

        var picked = setupFocusPicker(wandStack, focusStacks, equipped, shouldPickNext);
        if (picked != null) {
            if (belt.isPresent()) {
                var slot = belt
                        .map(WizcraftItems.FOCUSES_BELT::getOrCreateInventoryComponent)
                        .map(component -> component.getSlotWithStack(picked))
                        .orElse(-1);
                ClientPlayNetworking.send(new ChangeWandFocusC2SPayload(WandFocusDestination.FOCUSES_BELT, slot));
            } else {
                var slot = inventory.findSlotMatchingItem(picked);
                ClientPlayNetworking.send(new ChangeWandFocusC2SPayload(WandFocusDestination.PLAYER_INVENTORY, slot));
            }
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
            return ItemStack.isSameItemSameComponents(equipped, picked) ? null : picked;
        }

        return null;
    }
}
