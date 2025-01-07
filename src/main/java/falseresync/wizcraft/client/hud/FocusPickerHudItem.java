package falseresync.wizcraft.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import falseresync.lib.client.BetterDrawContext;
import falseresync.wizcraft.client.WizcraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4fStack;

import java.util.LinkedList;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class FocusPickerHudItem implements HudItem {
    protected static final Identifier SELECTION_TEX = wid("textures/hud/wand/focus_picker_selection.png");
    private static final int MARGIN = 2;
    private static final int SEL_TEX_W = 22;
    private static final int SEL_TEX_H = 22;
    private static final int ITEM_W = 16;
    private static final int ITEM_H = 16;
    private static final int TEXT_H = 8;
    private final MinecraftClient client;
    private final TextRenderer textRenderer;
    private LinkedList<ItemStack> focuses = new LinkedList<>();
    private int remainingDisplayTicks = 0;
    private float opacity = 1;

    public FocusPickerHudItem(MinecraftClient client, TextRenderer textRenderer) {
        this.client = client;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(BetterDrawContext context, RenderTickCounter tickCounter) {
        if (isVisible()) {
            opacity = Math.min(1, remainingDisplayTicks / 10f);
            var yOffsetPerItem = ITEM_H + MARGIN;
            var yOffset = (Math.min(focuses.size(), 3) - 1) * yOffsetPerItem;
            var widgetW = SEL_TEX_W;
            var widgetH = SEL_TEX_H + yOffset;

            var chargeDisplay = WizcraftClient.getHud().getWandChargeDisplay();
            var x = 4 + (chargeDisplay.isVisible() ? chargeDisplay.getWidth() : 0);
            var y = context.getScaledWindowHeight() / 2 - widgetH / 2;

            context.enableScissor(x, y, x + widgetW, y + widgetH);
            context.setShaderColor(1, 1, 1, opacity);

            var selTexX = x;
            var selTexY = y + yOffset;
            context.drawSquare(SELECTION_TEX, selTexX, selTexY, 22);

            var itemX = x + widgetW / 2 - ITEM_W / 2;

            var selected = focuses.peekFirst();
            var item1Y = y + SEL_TEX_H / 2 - ITEM_H / 2 + yOffset;
            context.drawItemWithoutEntity(selected, itemX, item1Y);

            if (focuses.size() > 1) {
                var next = focuses.get(1);
                var item2Y = y + Math.min(focuses.size() - 2, 1) * yOffsetPerItem + yOffsetPerItem / 2 - ITEM_H / 2;
                paintScaledTinted(context, next, itemX, item2Y, 0.85f, false);
            }

            if (focuses.size() > 2) {
                var next2 = focuses.get(2);
                var item3Y = y + yOffsetPerItem / 2 - ITEM_H / 2;
                paintScaledTinted(context, next2, itemX, item3Y, 0.70f, true);
            }

            context.disableScissor();
        }
    }

    protected void paintScaledTinted(BetterDrawContext context, ItemStack stack, int x, int y, float scale, boolean shouldTint) {
        Matrix4fStack view = RenderSystem.getModelViewStack();
        view.pushMatrix();
        view.scaleAround(scale, scale, 1f, x + ITEM_W / 2f, y + ITEM_H / 2f, 0);
        RenderSystem.applyModelViewMatrix();

        if (shouldTint) context.setShaderColor(161 / 255f, 158 / 255f, 170 / 255f, opacity / 2);

        context.drawItemWithoutEntity(stack, x, y);

        if (shouldTint) context.setShaderColor(1, 1, 1, opacity);

        view.popMatrix();
        RenderSystem.applyModelViewMatrix();
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
    }

    public void show() {
        remainingDisplayTicks = 80;
    }

    public void hide() {
        remainingDisplayTicks = 0;
        focuses.clear();
    }

    public void upload(LinkedList<ItemStack> newFocuses) {
        if (focuses.isEmpty()) {
            focuses = newFocuses;
        } else if (newFocuses.size() != focuses.size()) {
            var currentlyPicked = focuses.peekFirst();
            focuses = newFocuses;
            for (ItemStack focus : focuses) {
                if (ItemStack.areItemsAndComponentsEqual(focus, currentlyPicked)) {
                    focuses.remove(focus);
                    break;
                }
            }
            focuses.addFirst(currentlyPicked);
        }
    }

    public void pickNext() {
        focuses.addLast(focuses.removeFirst());
    }

    public ItemStack getCurrentlyPicked() {
        return focuses.peekFirst();
    }

    public boolean isVisible() {
        return remainingDisplayTicks > 0 && !focuses.isEmpty();
    }
}