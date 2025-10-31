package falseresync.wizcraft.client.gui;

import falseresync.wizcraft.common.data.ContainerComponent;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class WizcraftGui {
    public static void init() {
        TooltipComponentCallback.EVENT.register(data -> data instanceof ContainerComponent component ? new ContainerComponentTooltip(component) : null);
    }
}
