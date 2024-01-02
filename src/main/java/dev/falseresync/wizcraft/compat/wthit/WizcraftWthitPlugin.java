package dev.falseresync.wizcraft.compat.wthit;

import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import mcp.mobius.waila.api.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class WizcraftWthitPlugin implements IWailaPlugin, IEventListener {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addEventListener(this);
    }

    @Override
    public void onBeforeTooltipRender(DrawContext ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config, Canceller canceller) {
        if (WizHud.Slots.UNDER_BOSS_BAR.isOccupied()) {
            canceller.cancel();
        }
    }
}
