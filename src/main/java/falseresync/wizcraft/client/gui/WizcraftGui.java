package falseresync.wizcraft.client.gui;

import falseresync.wizcraft.common.data.InventoryComponent;
import net.fabricmc.fabric.api.client.rendering.v1.*;

public class WizcraftGui {
    public static void init() {
        TooltipComponentCallback.EVENT.register(data -> data instanceof InventoryComponent component ? new InventoryComponentTooltip(component) : null);
    }
}
