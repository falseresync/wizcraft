package falseresync.wizcraft.client.hud;

import com.mojang.blaze3d.systems.*;
import falseresync.lib.client.*;
import falseresync.lib.math.*;
import falseresync.wizcraft.client.*;
import net.minecraft.client.*;
import net.minecraft.client.font.*;
import net.minecraft.client.render.*;
import net.minecraft.component.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.joml.*;

import java.util.*;

import static falseresync.wizcraft.common.Wizcraft.*;

import java.lang.Math;

public class FocusPickerHudItem implements HudItem {
    protected static final Identifier SELECTION_TEX = wid("textures/hud/wand/focus_picker_selection.png");
    private static final int MARGIN = 2;
    private static final int SEL_TEX_W = 22;
    private static final int SEL_TEX_H = 22;
    private static final int ITEM_W = 16;
    private static final int ITEM_H = 16;
    private static final int TEXT_H = 8;
    private static final int DISPLAY_DURATION = 60;
    private static final int PARENT_ANIMATION_DURATION = 8;
    private static final int ITEMS_ANIMATION_DURATION = 6;
    private final MinecraftClient client;
    private final TextRenderer textRenderer;
    private ItemStack wand;
    private LinkedList<ItemStack> focuses = new LinkedList<>();
    private float baseOpacity = 1;
    private boolean isVisible = false;
    private int remainingDisplayTicks = 0;
    private boolean animatingParent = false;
    private int remainingParentAnimationTicks = 0;
    private boolean animatingItems = false;
    private int remainingItemsAnimationTicks = 0;

    public FocusPickerHudItem(MinecraftClient client, TextRenderer textRenderer) {
        this.client = client;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(BetterDrawContext context, RenderTickCounter tickCounter) {
        if (isVisible() || animatingParent) {
            baseOpacity = getAnimatedBaseOpacity();
            var yOffsetPerItem = ITEM_H + MARGIN;
            var yOffset = (Math.min(focuses.size(), 3) - 1) * yOffsetPerItem;
            var widgetW = SEL_TEX_W;
            var widgetH = SEL_TEX_H + yOffset;

            var chargeDisplay = WizcraftClient.getHud().getChargeDisplay();
            var x = 4 + (chargeDisplay.isVisible() ? chargeDisplay.getWidth() : 0);
            var y = context.getScaledWindowHeight() / 2 - widgetH / 2;

            RenderSystem.enableBlend();
            context.setShaderColor(1, 1, 1, baseOpacity);

            var selTexX = x;
            var selTexY = y + yOffset;
            context.drawSquare(SELECTION_TEX, selTexX, selTexY, 22);

            var itemX = x + widgetW / 2 - ITEM_W / 2;

            if (animatingItems) {
                var item1 = addGlintIfNecessary(focuses.peekLast());
                int item1Y = y + SEL_TEX_H / 2 - ITEM_H / 2 + yOffset;
                float item1Scale = (float) Easing.easeOutCirc((double) (remainingItemsAnimationTicks) / ITEMS_ANIMATION_DURATION);
                float item1Translation = (float) (SEL_TEX_H * Easing.easeInSine((double) (ITEMS_ANIMATION_DURATION - remainingItemsAnimationTicks) / ITEMS_ANIMATION_DURATION));
                float item1Opacity = baseOpacity * remainingItemsAnimationTicks / ITEMS_ANIMATION_DURATION;
                paintItem(context, item1, itemX, item1Y, item1Scale, item1Translation, item1Opacity, false);

                if (focuses.size() > 1) {
                    var item2 = focuses.peekFirst();
                    int item2Y = y + Math.min(focuses.size() - 2, 1) * yOffsetPerItem + yOffsetPerItem / 2 - ITEM_H / 2;
                    float item2Scale = 0.85f + (1 - 0.85f) * (float) Easing.easeOutSine((double) (ITEMS_ANIMATION_DURATION - remainingItemsAnimationTicks) / ITEMS_ANIMATION_DURATION);
                    float item2Translation = (float) (yOffsetPerItem * Easing.easeInOutSine((double) (ITEMS_ANIMATION_DURATION - remainingItemsAnimationTicks) / ITEMS_ANIMATION_DURATION));
                    paintItem(context, item2, itemX, item2Y, item2Scale, item2Translation, baseOpacity, false);
                }

                if (focuses.size() > 2) {
                    var item3 = focuses.get(1);
                    int item3Y = y + yOffsetPerItem / 2 - ITEM_H / 2;
                    float item3Scale = 0.70f * (float) Easing.easeOutSine((double) (ITEMS_ANIMATION_DURATION - remainingItemsAnimationTicks) / ITEMS_ANIMATION_DURATION);
                    float item3Translation = (float) (yOffsetPerItem * Easing.easeInOutSine((double) (ITEMS_ANIMATION_DURATION - remainingItemsAnimationTicks) / ITEMS_ANIMATION_DURATION));
                    paintItem(context, item3, itemX, item3Y, item3Scale, item3Translation, baseOpacity, false);

                    var item4 = focuses.get(2);
                    int item4Y = y + yOffsetPerItem / 2 - ITEM_H / 2;
                    float item4Scale = 0.70f * (float) Easing.easeInOutQuad((double) (ITEMS_ANIMATION_DURATION - remainingItemsAnimationTicks) / ITEMS_ANIMATION_DURATION);
                    paintItem(context, item4, itemX, item4Y, item4Scale, 0f, baseOpacity / 2, true);
                }
            } else {
                var item1 = addGlintIfNecessary(focuses.peekFirst());
                var item1Y = y + SEL_TEX_H / 2 - ITEM_H / 2 + yOffset;
                paintItem(context, item1, itemX, item1Y, 1f, 0f, baseOpacity, false);

                if (focuses.size() > 1) {
                    var item2 = focuses.get(1);
                    var item2Y = y + Math.min(focuses.size() - 2, 1) * yOffsetPerItem + yOffsetPerItem / 2 - ITEM_H / 2;
                    paintItem(context, item2, itemX, item2Y, 0.85f, 0f, baseOpacity, false);
                }

                if (focuses.size() > 2) {
                    var item3 = focuses.get(2);
                    var item3Y = y + yOffsetPerItem / 2 - ITEM_H / 2;
                    paintItem(context, item3, itemX, item3Y, 0.70f, 0f, baseOpacity, true);
                }
            }

            RenderSystem.disableBlend();
        }
    }

    protected ItemStack addGlintIfNecessary(ItemStack stack) {
        ItemStack stackWithGlint = null;
        if (wand.hasGlint() && stack != null) {
            stackWithGlint = stack.copy();
            stackWithGlint.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        return stackWithGlint == null ? stack : stackWithGlint;
    }

    protected void paintItem(BetterDrawContext context, ItemStack stack, int x, int y, float scale, float translation, float opacity, boolean shouldTint) {
        var matrices = context.getMatrices();
        matrices.push();
        matrices.multiplyPositionMatrix(new Matrix4f().scaleAround(scale, scale, 1f, x + ITEM_W / 2f, y + ITEM_H / 2f, 0));
        matrices.translate(0, translation, 0);
        if (shouldTint) {
            context.setShaderColor(161 / 255f, 158 / 255f, 170 / 255f, opacity);
        } else {
            context.setShaderColor(1, 1, 1, opacity);
        }

        context.drawItemWithoutEntity(stack, x, y);

        context.setShaderColor(1, 1, 1, baseOpacity);
        matrices.pop();
    }

    private float getAnimatedBaseOpacity() {
        if (animatingParent) {
            return isVisible()
                    ? 1 - (float) remainingParentAnimationTicks / PARENT_ANIMATION_DURATION
                    : (float) remainingParentAnimationTicks / PARENT_ANIMATION_DURATION;
        }
        return 1;
    }

    @Override
    public void tick() {
        if (client.player == null) {
            clear();
            return;
        }

        if (remainingDisplayTicks > 0) {
            remainingDisplayTicks -= 1;

            if (remainingDisplayTicks == 0) {
                hide();
            }
        }

        if (remainingParentAnimationTicks > 0) {
            remainingParentAnimationTicks -= 1;

            if (remainingParentAnimationTicks == 0) {
                animatingParent = false;

                if (remainingDisplayTicks == 0) {
                    clear();
                }
            }
        }

        if (remainingItemsAnimationTicks > 0) {
            remainingItemsAnimationTicks -= 1;

            if (remainingItemsAnimationTicks == 0) {
                animatingItems = false;
            }
        }
    }

    public void show() {
        if (!isVisible()) {
            animateParent();
        }
        isVisible = true;
        remainingDisplayTicks = DISPLAY_DURATION;
    }

    public void hide() {
        if (isVisible()) {
            animateParent();
        }
        isVisible = false;
    }

    private void clear() {
        isVisible = false;
        focuses.clear();
    }

    private void animateParent() {
        animatingParent = true;
        remainingParentAnimationTicks = PARENT_ANIMATION_DURATION;
    }

    private void animateItems() {
        animatingItems = true;
        remainingItemsAnimationTicks = ITEMS_ANIMATION_DURATION;
    }

    /**
     * @param wand has to be the same stack, not a copy
     */
    public void upload(ItemStack wand, LinkedList<ItemStack> newFocuses) {
        this.wand = wand;
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
        if (focuses.size() > 1) {
            focuses.addLast(focuses.removeFirst());
            animateItems();
        }
    }

    public ItemStack getCurrentlyPicked() {
        return focuses.peekFirst();
    }

    public boolean isVisible() {
        return isVisible && !focuses.isEmpty() && wand != null;
    }
}