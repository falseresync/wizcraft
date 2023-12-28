package dev.falseresync.wizcraft.client.gui.hud.widget;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * @author Algo copied almost wholesale from LibGui
 */
@Environment(EnvType.CLIENT)
public class WWandChargeBar extends WWidget implements WControllerAware {
    public static final Identifier BAR = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/charge_bar.png");
    public static final Identifier OVERLAY = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/charge_bar_overlay.png");
    protected final Texture barTex;
    protected final Texture overlayTex;
    protected final int max;
    protected int remainingDisplayTicks = 0;
    protected int value;

    public WWandChargeBar(SkyWand wand) {
        this.barTex = new Texture(BAR);
        this.overlayTex = new Texture(OVERLAY).withUv(0.25f, 0f, 0.75f, 1f);
        this.value = wand.getCharge();
        this.max = wand.getMaxCharge();
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (remainingDisplayTicks == 0) {
            return;
        }
        var opacity = Math.min(1, remainingDisplayTicks / 10F);

        ScreenDrawing.texturedRect(context, x, y, 64, 16, barTex, Color.WHITE.toRgb(), opacity);

        float percent = MathHelper.clamp((float) value / max, 0, 1);
        int barMax = 32;
        percent = ((int) (percent * barMax)) / (float) barMax; //Quantize to bar size
        int barSize = (int) (barMax * percent);
        if (barSize <= 0) return;

        var clippedTex = overlayTex.withUv(overlayTex.u1(), overlayTex.v1(), MathHelper.lerp(percent, overlayTex.u1(), overlayTex.u2()), overlayTex.v2());
        ScreenDrawing.texturedRect(context, x + 16, y, barSize, 16, clippedTex, Color.WHITE.toRgb(), opacity);
    }

    @Override
    public void controllerTick(int remainingDisplayTicks) {
        this.remainingDisplayTicks = remainingDisplayTicks;
    }

    public void updateValue(int value) {
        this.value = value;
    }
}
