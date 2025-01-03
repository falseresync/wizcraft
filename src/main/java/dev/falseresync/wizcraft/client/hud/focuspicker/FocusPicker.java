package dev.falseresync.wizcraft.client.hud.focuspicker;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.falseresync.wizcraft.api.client.BetterDrawContext;
import dev.falseresync.wizcraft.api.client.HudItem;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayDeque;
import java.util.Deque;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class FocusPicker implements HudItem {
    protected static final Identifier SELECTION_TEX = wid("textures/hud/wand/focus_picker_selection.png");
    protected static final Identifier HINT_LEFT_TEX = wid("textures/hud/wand/focus_picker_hint_left.png");
    protected static final Identifier HINT_RIGHT_TEX = wid("textures/hud/wand/focus_picker_hint_right.png");
    private final MinecraftClient client;
    private final TextRenderer textRenderer;
    private Deque<FocusStack> focuses = new ArrayDeque<>();
    private int remainingDisplayTicks = 0;

    public FocusPicker(MinecraftClient client, TextRenderer textRenderer) {
        this.client = client;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(BetterDrawContext context, RenderTickCounter tickCounter) {
        if (isVisible()) {
            float opacity = Math.min(1, remainingDisplayTicks / 10f);
            var centerX = context.getScaledWindowWidth() / 2;
            var x = centerX - 9;
            var y = 4;

            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, opacity);

            context.drawSquare(SELECTION_TEX, calcX(x, 22), calcY(y, 22), 22);

            var labelY = y + 18 + 4;
            context.drawCenteredTextWithShadow(textRenderer, getPicked().toItemStack().getName(), centerX, labelY, BetterDrawContext.NO_TINT);

            var tooltip = getPicked().getFocus().getTooltip(client.player, Item.TooltipContext.DEFAULT);
            var tooltipY = labelY + textRenderer.fontHeight + 2;
            for (int i = 0; i < tooltip.size(); i++) {
                context.drawCenteredTextWithShadow(textRenderer, tooltip.get(i), centerX, tooltipY + i * textRenderer.fontHeight, BetterDrawContext.NO_TINT);
            }

            switch (focuses.size()) {
                case 1 -> paint1(context, x, y);
                case 2 -> paint2(context, x, y);
                case 3 -> paint3(context, x, y);
                default -> paintMany(context, x, y);
            }

            RenderSystem.disableBlend();
        }
    }

    protected int calcX(int x, int width) {
        return x + 9 - (width / 2);
    }

    protected int calcY(int y, int height) {
        return y + 9 - (height / 2);
    }

    protected void paint1(BetterDrawContext context, int x, int y) {
        var selected = focuses.peekFirst();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(selected.toItemStack(), calcX(x, 16) - 20, calcY(y, 16));
    }

    protected void paint2(BetterDrawContext context, int x, int y) {
        // Polling here allows to peek the next focus
        var selected = focuses.pollFirst();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(selected.toItemStack(), calcX(x, 16), calcY(y, 16));

        var next = focuses.peekFirst();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(next.toItemStack(), calcX(x, 16) + 20, calcY(y, 16));

        // Reset the deque
        focuses.offerFirst(selected);
    }

    protected void paint3(BetterDrawContext context, int x, int y) {
        var last = focuses.peekLast();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(last.toItemStack(), calcX(x, 16) - 20, calcY(y, 16));

        paint2(context, x, y);
    }

    protected void paintMany(BetterDrawContext context, int x, int y) {
        context.drawSquare(HINT_LEFT_TEX, calcX(x, 16) - 36, calcY(y, 16), 16);
        context.drawSquare(HINT_RIGHT_TEX, calcX(x, 16) + 36, calcY(y, 16), 16);
        paint3(context, x, y);
    }

    @Override
    public void tick() {
        if (isVisible()) {
            remainingDisplayTicks -= 1;
        }
    }

    public FocusStack update(Deque<FocusStack> focuses) {
        Preconditions.checkArgument(!focuses.isEmpty(), "Focus picker expects at least one FocusStack, got none");
        resetDisplayTicks();
        setFocusesIfDifferent(focuses);
        pickNext();
        return getPicked();
    }

    public boolean isVisible() {
        return remainingDisplayTicks > 0 && !focuses.isEmpty();
    }

    public void pickNext() {
        if (isVisible()) {
            this.focuses.offerLast(this.focuses.pollFirst());
        }
    }

    public FocusStack getPicked() {
        return focuses.peekFirst();
    }

    private void setFocusesIfDifferent(Deque<FocusStack> focuses) {
        if (this.focuses.isEmpty()) {
            this.focuses = focuses;
            return;
        }

        var previouslyPicked = getPicked();
        this.focuses = focuses;
        while (previouslyPicked.getFocus().getType() != getPicked().getFocus().getType()) {
            pickNext();
        }

//        // If the player has picked up a focus while the picker was open, the deque should be updated
//        // Assume that the focuses didn't change if their count didn't change
//        if (this.focuses.size() != focuses.size()) {
//            var picked = this.focuses.peekFirst();
//            this.focuses = focuses;
//            if (picked == null) return;
//            while (picked.equals(this.focuses.peekFirst())) {
//                pickNext();
//            }
//        }
    }

    private void resetDisplayTicks() {
        remainingDisplayTicks = 80;
    }
}
