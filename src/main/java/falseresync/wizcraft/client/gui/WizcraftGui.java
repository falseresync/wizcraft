package falseresync.wizcraft.client.gui;

import falseresync.wizcraft.common.data.component.FocusesBeltComponent;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class WizcraftGui {
    public static void init() {
        TooltipComponentCallback.EVENT.register(data -> data instanceof FocusesBeltComponent component ? new FocusesBeltTooltipComponent(component) : null);
    }
}
