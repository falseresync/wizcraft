package falseresync.wizcraft.client.gui;

import falseresync.wizcraft.common.data.component.InventoryComponent;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class WizcraftGui {
    public static void init() {
        TooltipComponentCallback.EVENT.register(data -> data instanceof InventoryComponent component ? new InventoryComponentTooltip(component) : null);
    }
}
