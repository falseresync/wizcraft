package dev.falseresync.wizcraft.client.gui.hud.widget;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.ControllerAwareWidget;
import dev.falseresync.wizcraft.client.gui.DrawingExt;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.HudController;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
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
public class WWandChargeBar extends WWidget implements ControllerAwareWidget {
    protected static final int BAR_WIDTH = 64;
    protected static final int BAR_HEIGHT = 16;
    protected static final int OVERLAY_WIDTH = 32;
    protected static final int OVERLAY_X_OFFSET = 16;
    protected static final float OVERLAY_U1 = (float) OVERLAY_X_OFFSET / BAR_WIDTH;
    protected static final float OVERLAY_U2 = (float) (OVERLAY_X_OFFSET + OVERLAY_WIDTH) / BAR_WIDTH;
    public static final Identifier BAR = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/charge_bar.png");
    public static final Identifier OVERLAY = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/charge_bar_overlay.png");
    protected final Texture barTex;
    protected final Texture overlayTex;
    protected final int max;
    protected int value;
    protected HudController<?, ?> controller = null;

    public WWandChargeBar(SkyWand wand) {
        this.barTex = new Texture(BAR);
        this.overlayTex = new Texture(OVERLAY)
                .withUv(OVERLAY_U1, 0f, OVERLAY_U2, 1f);
        this.value = wand.getCharge();
        this.max = wand.getMaxCharge();
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (this.controller == null || this.controller.getRemainingDisplayTicks() == 0) {
            return;
        }
        var opacity = Math.min(1, this.controller.getRemainingDisplayTicks() / 10F);

        ScreenDrawing.texturedRect(context, x, y, BAR_WIDTH, BAR_HEIGHT, this.barTex, DrawingExt.WHITE_TINT, opacity);

        float percent = MathHelper.clamp((float) this.value / this.max, 0, 1);
        percent = ((int) (percent * OVERLAY_WIDTH)) / (float) OVERLAY_WIDTH; // Quantize to overlay size
        var clippedOverlayWidth = (int) (OVERLAY_WIDTH * percent);
        if (clippedOverlayWidth <= 0) return;

        var clippedTex = this.overlayTex.withUv(
                this.overlayTex.u1(),
                this.overlayTex.v1(),
                MathHelper.lerp(percent, this.overlayTex.u1(), this.overlayTex.u2()),
                this.overlayTex.v2());
        ScreenDrawing.texturedRect(context, x + OVERLAY_X_OFFSET, y, clippedOverlayWidth, BAR_HEIGHT, clippedTex, DrawingExt.WHITE_TINT, opacity);
    }

    public void updateValue(int value) {
        this.value = value;
    }

    @Override
    public void setController(HudController<?, ?> controller) {
        this.controller = controller;
    }
}
