package falseresync.wizcraft.client;

import net.fabricmc.fabric.api.event.*;
import net.minecraft.entity.player.*;

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

    @FunctionalInterface
    public interface ContentsChanged {
        void onChanged(PlayerInventory inventory);
    }

    @FunctionalInterface
    public interface SelectedSlotChanged {
        void onChanged(PlayerInventory inventory, int lastSelectedSlot);
    }
}
