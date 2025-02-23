package falseresync.wizcraft.client.hud;

import com.mojang.blaze3d.systems.*;
import falseresync.lib.client.*;
import falseresync.lib.math.*;
import falseresync.wizcraft.common.data.attachment.*;
import falseresync.wizcraft.common.data.component.*;
import net.minecraft.client.*;
import net.minecraft.client.font.*;
import net.minecraft.client.render.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class ChargeDisplayHudItem implements HudItem {
    protected static final Identifier BAR_TEX = wid("textures/hud/wand/charge_bar.png");
    protected static final Identifier OVERLAY_TEX = wid("textures/hud/wand/charge_bar_overlay.png");
    protected static final Identifier SHELL_TEX = wid("textures/hud/wand/charge_shell.png");
    private static final int WIDGET_W = 16;
    private static final int WIDGET_H = 64;
    private static final int TEX_W = 16;
    private static final int TEX_H = 64;
    private static final int SHELL_W = 16;
    private static final int SHELL_H = 16;
    private static final int OVERLAY_U = 7;
    private static final int OVERLAY_V = 16;
    private static final int OVERLAY_W = 2;
    private static final int OVERLAY_H = 32;
    private static final int ANIMATION_DURATION = 10;
    private static final int SHELL_NO_CHARGE_TINT = 0xAA_FF_FF_FF;
    private static final int SHELL_FULL_CHARGE_TINT = 0xFF_00_BF_FF;
    private final MinecraftClient client;
    private final TextRenderer textRenderer;
    private int currentCharge = 0;
    private int maxCharge = 0;
    private int chargeInShells = -1;
    private int maxChargeInShells = -1;
    private boolean isVisible = false;
    private ItemStack wand;
    private boolean animating = false;
    private int remainingAnimationTicks = 0;

    public ChargeDisplayHudItem(MinecraftClient client, TextRenderer textRenderer) {
        this.client = client;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(BetterDrawContext context, RenderTickCounter tickCounter) {
        if (isVisible() || animating) {
            float opacity = getAnimatedOpacity();
            float x = getAnimatedX();
            float y = context.getScaledWindowHeight() / 2f - TEX_H / 2f;

            RenderSystem.enableBlend();
            context.setShaderColor(1, 1, 1, opacity);

            if (maxChargeInShells >= 0) {
                var tint = ColorHelper.Argb.lerp((float) chargeInShells / maxChargeInShells, SHELL_NO_CHARGE_TINT, SHELL_FULL_CHARGE_TINT);
                context.setShaderColor(
                        ColorHelper.Argb.getRed(tint) / 255f, ColorHelper.Argb.getGreen(tint) / 255f,
                        ColorHelper.Argb.getBlue(tint) / 255f, ColorHelper.Argb.getAlpha(tint) / 255f * opacity);
                context.drawNonDiscreteRect(SHELL_TEX, x, y - SHELL_H, SHELL_W, SHELL_H);
                context.setShaderColor(1, 1, 1, opacity);
            }

            context.drawNonDiscreteRect(BAR_TEX, x, y, TEX_W, TEX_H);

            var step = getStep();
            var v = OVERLAY_V + step;
            var h = OVERLAY_H - step;
            context.drawNonDiscreteRect(OVERLAY_TEX, x, y, OVERLAY_U, v, OVERLAY_W, h, TEX_W, TEX_H);

            RenderSystem.disableBlend();
        }
    }

    private float getAnimatedOpacity() {
        if (animating) {
            return isVisible()
                    ? 1 - (float) remainingAnimationTicks / ANIMATION_DURATION
                    : (float) remainingAnimationTicks / ANIMATION_DURATION;
        }
        return 1;
    }

    private float getAnimatedX() {
        if (animating) {
            return isVisible()
                    ? (float) (2 - remainingAnimationTicks * Easing.easeInOutCubic((double) remainingAnimationTicks / ANIMATION_DURATION))
                    : (float) (2 - (ANIMATION_DURATION - remainingAnimationTicks * Easing.easeInOutCubic((double) remainingAnimationTicks / ANIMATION_DURATION)));
        }

        return 2;
    }

    private int getStep() {
        return Math.clamp(Math.round(OVERLAY_H - (float) (currentCharge * OVERLAY_H) / maxCharge), 0, OVERLAY_H);
    }

    @Override
    public void tick() {
        if (client.player == null) {
            clear();
            return;
        }

        if (wand != null) {
            currentCharge = wand.getOrDefault(WizcraftComponents.WAND_CHARGE, 0);
            maxCharge = wand.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);

            var shells = client.player.getAttached(WizcraftAttachments.CHARGE_SHELLS);
            if (shells != null) {
                chargeInShells = shells.currentCharge();
                maxChargeInShells = shells.maxCharge();
            }
        }

        if (remainingAnimationTicks > 0) {
            remainingAnimationTicks -= 1;

            if (remainingAnimationTicks == 0) {
                animating = false;
                if (!isVisible()) {
                    clear();
                }
            }
        }
    }

    public void show() {
        if (!isVisible) {
            animate();
        }
        isVisible = true;
    }

    public void hide() {
        if (isVisible()) {
            animate();
        }
        isVisible = false;
        wand = null;
    }

    private void clear() {
        isVisible = false;
        wand = null;
        currentCharge = 0;
        maxCharge = 0;
        chargeInShells = 0;
        maxChargeInShells = 0;
    }

    private void animate() {
        animating = true;
        remainingAnimationTicks = ANIMATION_DURATION;
    }

    public void upload(ItemStack stack) {
        wand = stack;
    }

    public boolean isVisible() {
        return isVisible && wand != null;
    }

    public int getWidth() {
        return WIDGET_W;
    }
}
