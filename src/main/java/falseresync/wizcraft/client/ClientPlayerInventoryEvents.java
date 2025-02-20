package falseresync.wizcraft.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;

public class ClientPlayerInventoryEvents {
    public static final Event<ContentsChanged> CONTENTS_CHANGED = EventFactory.createArrayBacked(
            ContentsChanged.class,
            listeners -> inventory -> {
                for (var listener : listeners) {
                    listener.onChanged(inventory);
                }
            });
    public static final Event<SelectedSlotChanged> SELECTED_SLOT_CHANGED = EventFactory.createArrayBacked(
            SelectedSlotChanged.class,
            listeners -> (inventory, lastSelectedSlot) -> {
                for (var listener : listeners) {
                    listener.onChanged(inventory, lastSelectedSlot);
                }
            });


    public static void init() {
    }

    public interface ContentsChanged {
        void onChanged(PlayerInventory inventory);
    }

    public interface SelectedSlotChanged {
        void onChanged(PlayerInventory inventory, int lastSelectedSlot);
    }
}
