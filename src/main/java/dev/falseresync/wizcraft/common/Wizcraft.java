package dev.falseresync.wizcraft.common;

import dev.falseresync.wizcraft.common.entity.WizEntityTypes;
import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import dev.falseresync.wizcraft.network.UpdateSkyWandC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MODID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("wizcraft");

    @Override
    public void onInitialize() {
        WizRegistries.register();
        WizItems.registerItems((id, item) -> Registry.register(Registries.ITEM, id, item));
        WizItems.registerItemGroups((id, item) -> Registry.register(Registries.ITEM_GROUP, id, item));
        WizEntityTypes.register((id, entityType) -> Registry.register(Registries.ENTITY_TYPE, id, entityType));
        WizFocuses.register((id, focus) -> Registry.register(WizRegistries.FOCUSES, id, focus));

        ServerPlayNetworking.registerGlobalReceiver(UpdateSkyWandC2SPacket.TYPE, (packet, player, responseSender) -> {
            var inventory = player.getInventory();
            var mainHandStack = inventory.getMainHandStack();
            if (!mainHandStack.isOf(WizItems.SKY_WAND)) {
                throw new IllegalStateException("Must not update sky wand if it's not in the main hand");
            }

            var wand = SkyWand.fromStack(mainHandStack);

            var pickedFocusStack = packet.pickedFocus().toStack();
            var slot = inventory.getSlotWithStack(pickedFocusStack);

            // TODO: needs to be redone, possibly with Storage API
            if (slot != -1) {
                var activeFocus = wand.getActiveFocus();
                if (activeFocus.getType() == WizFocuses.CHARGING) {
                    inventory.removeOne(pickedFocusStack);
                } else {
                    inventory.setStack(slot, activeFocus.asStack());
                }
            }

            // Hopefully this *should* be guaranteed to be a FocusItem
            // Otherwise liquid shit's hitting the fan
            wand.switchFocus((FocusItem) pickedFocusStack.getItem());
            wand.saveToStack(mainHandStack);
        });
    }
}