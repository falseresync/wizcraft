package falseresync.wizcraft.client;

import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import falseresync.wizcraft.client.hud.FocusPickerHudItem;
import falseresync.wizcraft.client.hud.WandChargeDisplayHudItem;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import falseresync.wizcraft.networking.c2s.ChangeWandFocusC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

public class WandManager {
    private final WandChargeDisplayHudItem chargeDisplay;
    private final FocusPickerHudItem focusPicker;

    public WandManager() {
        chargeDisplay = WizcraftClient.getHud().getWandChargeDisplay();
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
        var equippedFocusStack = wandStack.getOrDefault(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        var focusStacks = inventory.main.stream()
                .filter(it -> it.isIn(WizcraftItemTags.FOCUSES))
                .collect(Collectors.toCollection(LinkedList::new));
        // TODO: integrate focuses belt into selection
//        var focusesBeltStacks = TrinketsApi.getTrinketComponent(inventory.player)
//                .map(trinketComponent -> trinketComponent.getEquipped(WizcraftItems.FOCUSES_BELT).getFirst().getRight())
//                .flatMap(stack -> Optional.ofNullable(stack.get(WizcraftDataComponents.FOCUSES_BELT)))
//                .map(focusesBeltComponent -> new LinkedList<>(focusesBeltComponent.heldStacks))
//                .orElseGet(LinkedList::new);

        if (!equippedFocusStack.isEmpty()) {
            focusStacks.addFirst(equippedFocusStack);
        }

        if (focusStacks.isEmpty()) {
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.translatable("hud.wizcraft.wand.no_focuses"), false);
            return;
        }

        setupFocusPicker(inventory, focusStacks, equippedFocusStack, shouldPickNext);
    }

    private void setupFocusPicker(PlayerInventory inventory, LinkedList<ItemStack> focusStacks, ItemStack equippedFocusStack, boolean shouldPickNext) {
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
            if (slot != -1) {
                ClientPlayNetworking.send(new ChangeWandFocusC2SPacket(slot));
            }
        }
    }
}
