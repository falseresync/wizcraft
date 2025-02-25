package falseresync.wizcraft.client;

import dev.emi.trinkets.api.event.*;
import dev.emi.trinkets.payload.*;
import falseresync.wizcraft.client.hud.*;
import falseresync.wizcraft.common.data.attachment.*;
import falseresync.wizcraft.common.data.component.*;
import falseresync.wizcraft.common.item.*;
import falseresync.wizcraft.networking.c2s.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

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
                hideChargeDisplayIfShould(player);
            }
            return rule;
        });

        TrinketUnequipCallback.EVENT.register((stack, slot, entity) -> {
            if (entity instanceof PlayerEntity player) {
                hideChargeDisplayIfShould(player);
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
        if (player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)) {
            chargeDisplay.upload(wandStack);
            chargeDisplay.show();
        }
    }

    private void hideChargeDisplayIfShould(PlayerEntity player) {
        if (chargeDisplay.isVisible() && !player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)) {
            chargeDisplay.hide();
        }
    }

    private void scanInventoryAndSetupFocusPicker(PlayerInventory inventory, ItemStack wandStack, boolean shouldPickNext) {
        var equipped = wandStack.getOrDefault(WizcraftComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        var belt = WizcraftItems.FOCUSES_BELT.findTrinketStack(inventory.player);
        var focusStacks = belt
                .map(it -> WizcraftItems.FOCUSES_BELT.getOrCreateInventoryComponent(it).stacks().stream().filter(stack -> !stack.isEmpty()))
                .orElseGet(() -> inventory.main.stream().filter(it -> it.isIn(WizcraftItemTags.FOCUSES)))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!equipped.isEmpty()) {
            focusStacks.addFirst(equipped);
        }

        if (focusStacks.isEmpty()) {
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.translatable("hud.wizcraft.wand.no_focuses"), false);
            return;
        }

        var picked = setupFocusPicker(wandStack, focusStacks, equipped, shouldPickNext);
        if (picked != null) {
            if (belt.isPresent()) {
                var slot = belt
                        .map(WizcraftItems.FOCUSES_BELT::getOrCreateInventoryComponent)
                        .map(component -> component.getSlotWithStack(picked))
                        .orElse(-1);
                ClientPlayNetworking.send(new ChangeWandFocusC2SPacket(WandFocusDestination.FOCUSES_BELT, slot));
            } else {
                var slot = inventory.getSlotWithStack(picked);
                ClientPlayNetworking.send(new ChangeWandFocusC2SPacket(WandFocusDestination.PLAYER_INVENTORY, slot));
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
            return ItemStack.areItemsAndComponentsEqual(equipped, picked) ? null : picked;
        }

        return null;
    }
}
