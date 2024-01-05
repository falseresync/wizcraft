package dev.falseresync.wizcraft.client.gui.hud.widget;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.ControllerAwareWidget;
import dev.falseresync.wizcraft.client.gui.DrawingExt;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.HudController;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.FocusItem;
import dev.falseresync.wizcraft.common.skywand.focus.FocusStack;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @implNote The focuses deque is validated to have at least one stack, therefore using {@link Deque#getFirst()} is okay
 */
@Environment(EnvType.CLIENT)
public class WFocusPicker extends WWidget implements ControllerAwareWidget {
    protected static final int ITEM_OFFSET = 20;
    protected static final int ITEM_SIZE = 16;
    protected static final int HINT_OFFSET = 36;
    protected static final int HINT_SIZE = 16;
    protected static final int SELECTION_SIZE = 22;
    protected static final int LABEL_OFFSET = 4;
    protected static final int TOOLTIP_OFFSET = 2;
    protected static final int TOOLTIP_LINE_SPACING = 2;
    protected static final Identifier SELECTION_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_selection.png");
    protected static final Identifier HINT_LEFT_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_hint_left.png");
    protected static final Identifier HINT_RIGHT_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_hint_right.png");
    protected final Deque<FocusStack> focusStacks;
    protected final WLabelWithSFX label;
    protected final List<WLabelWithSFX> tooltip;
    protected HudController<?, ?> controller = null;

    public WFocusPicker(Deque<FocusStack> focusStacks) {
        Preconditions.checkArgument(!focusStacks.isEmpty(), "Focus picker expects at least one FocusStack, got none");
        this.focusStacks = focusStacks;
        label = new WLabelWithSFX(getPicked().toItemStack().getName());
        label.enableShadow();
        label.enableFade();
        label.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tooltip = new ArrayList<>();
        updateTooltip();
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (controller == null || controller.getRemainingDisplayTicks() == 0) return;

        float opacity = Math.min(1, controller.getRemainingDisplayTicks() / 10f);

        RenderSystem.enableDepthTest();

        DrawingExt.square(context, calcX(x, SELECTION_SIZE), calcY(y, SELECTION_SIZE), SELECTION_SIZE, SELECTION_TEX, opacity);
        var labelY = y + getHeight() + LABEL_OFFSET;
        label.paint(context, x, labelY, mouseX, mouseY);
        var tooltipBeginY = labelY + label.getHeight() + TOOLTIP_OFFSET;
        for (int i = 0; i < tooltip.size(); i++) {
            var w = tooltip.get(i);
            w.paint(context, x,  tooltipBeginY + (i - 1) * (w.getHeight() + TOOLTIP_LINE_SPACING), mouseX, mouseY);
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, opacity);

        switch (focusStacks.size()) {
            case 1 -> paint1(context, x, y);
            case 2 -> paint2(context, x, y);
            case 3 -> paint3(context, x, y);
            default -> paintMany(context, x, y);
        }

        RenderSystem.disableBlend();
    }

    protected int calcX(int x, int width) {
        return x + getWidth() / 2 - (width / 2);
    }

    protected int calcY(int y, int height) {
        return y + getHeight() / 2 - (height / 2);
    }

    protected void paint1(DrawContext context, int x, int y) {
        var selected = focusStacks.peekFirst();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(selected.toItemStack(), calcX(x, ITEM_SIZE) - ITEM_OFFSET, calcY(y, ITEM_SIZE));
    }

    protected void paint2(DrawContext context, int x, int y) {
        // Polling here allows to peek the next focus
        var selected = focusStacks.pollFirst();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(selected.toItemStack(), calcX(x, ITEM_SIZE), calcY(y, ITEM_SIZE));

        var next = focusStacks.peekFirst();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(next.toItemStack(), calcX(x, ITEM_SIZE) + ITEM_OFFSET, calcY(y, ITEM_SIZE));

        // Reset the deque
        focusStacks.offerFirst(selected);
    }

    protected void paint3(DrawContext context, int x, int y) {
        var last = focusStacks.peekLast();
        //noinspection DataFlowIssue
        context.drawItemWithoutEntity(last.toItemStack(), calcX(x, ITEM_SIZE) - ITEM_OFFSET, calcY(y, ITEM_SIZE));

        paint2(context, x, y);
    }

    protected void paintMany(DrawContext context, int x, int y) {
        DrawingExt.square(context, calcX(x, HINT_SIZE) - HINT_OFFSET, calcY(y, HINT_SIZE), HINT_SIZE, HINT_LEFT_TEX);
        DrawingExt.square(context, calcX(x, HINT_SIZE) + HINT_OFFSET, calcY(y, HINT_SIZE), HINT_SIZE, HINT_RIGHT_TEX);
        paint3(context, x, y);
    }

    protected void updateTooltip() {
        tooltip.clear();
        tooltip.addAll(getPicked().getFocus()
                .getTooltip(MinecraftClient.getInstance().player, TooltipContext.BASIC)
                .stream()
                .map(text -> {
                    var w = new WLabelWithSFX(text);
                    w.enableFade();
                    w.enableShadow();
                    w.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    return w;
                })
                .toList());
    }

    public void pickNext() {
        focusStacks.offerLast(focusStacks.pollFirst());
        label.setText(getPicked().toItemStack().getName());
        updateTooltip();
    }

    public FocusStack getPicked() {
        return focusStacks.peekFirst();
    }

    @Override
    public void addNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, focusStacks.getFirst().toItemStack().getName());
    }

    @Override
    public void setController(HudController<?, ?> controller) {
        this.controller = controller;
        label.setController(controller);
    }
}
