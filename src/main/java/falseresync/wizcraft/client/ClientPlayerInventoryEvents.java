package falseresync.wizcraft.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;

public class ClientPlayerInventoryEvents {
    public static final Event<Changed> CHANGED = EventFactory.createArrayBacked(
            Changed.class,
            listeners -> inventory -> {
                for (var listener : listeners) {
                    listener.onChanged(inventory);
                }
            });

    public static void init() {
    }

    public interface Changed {
        void onChanged(PlayerInventory inventory);
    }
}
