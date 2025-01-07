package falseresync.wizcraft.client.hud;

import falseresync.lib.client.BetterDrawContext;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WandChargeDisplayHudItem implements HudItem {
    protected static final Identifier BAR_TEX = wid("textures/hud/wand/charge_bar.png");
    protected static final Identifier OVERLAY_TEX = wid("textures/hud/wand/charge_bar_overlay.png");
    private static final int WIDGET_W = 16;
    private static final int WIDGET_H = 64;
    private static final int TEX_W = 16;
    private static final int TEX_H = 64;
    private static final int OVERLAY_U = 7;
    private static final int OVERLAY_V = 16;
    private static final int OVERLAY_W = 2;
    private static final int OVERLAY_H = 32;
    private final MinecraftClient client;
    private final TextRenderer textRenderer;
    private int currentCharge = 0;
    private int maxCharge = 0;
    private int remainingDisplayTicks = 0;
    private float opacity = 1;
    private ItemStack wand;

    public WandChargeDisplayHudItem(MinecraftClient client, TextRenderer textRenderer) {
        this.client = client;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(BetterDrawContext context, RenderTickCounter tickCounter) {
        if (isVisible()) {
            opacity = Math.min(1, remainingDisplayTicks / 10f);
            var x = 2;
            var y = context.getScaledWindowHeight() / 2 - TEX_H / 2;

            context.enableScissor(x, y, x + TEX_W, y + TEX_H);
            context.setShaderColor(1, 1, 1, opacity);

            context.drawRect(BAR_TEX, x, y, TEX_W, TEX_H, TEX_W, TEX_H);

            var step = getStep();
            var v = OVERLAY_V + step;
            var h = OVERLAY_H - step;
            context.drawTexture(OVERLAY_TEX, x + OVERLAY_U, y + v, OVERLAY_U, v, OVERLAY_W, h, TEX_W, TEX_H);

            context.disableScissor();
        }
    }

    private int getStep() {
        return Math.clamp(Math.round(OVERLAY_H - (float) (currentCharge * OVERLAY_H) / maxCharge), 0, OVERLAY_H);
    }

    @Override
    public void tick() {
        if (client.player == null) {
            hide();
            return;
        }

        if (remainingDisplayTicks > 0) {
            remainingDisplayTicks -= 1;

            if (remainingDisplayTicks == 0) {
                hide();
                return;
            }
        }

        if (wand != null) {
            currentCharge = wand.getOrDefault(WizcraftDataComponents.WAND_CHARGE, 0);
            maxCharge = wand.getOrDefault(WizcraftDataComponents.WAND_MAX_CHARGE, 0);
        }
    }

    public void show() {
        remainingDisplayTicks = 80;
    }

    public void hide() {
        remainingDisplayTicks = 0;
        wand = null;
    }

    public void upload(ItemStack stack) {
        wand = stack;
    }

    public boolean isVisible() {
        return remainingDisplayTicks > 0 && wand != null;
    }

    public int getWidth() {
        return WIDGET_W;
    }
}
