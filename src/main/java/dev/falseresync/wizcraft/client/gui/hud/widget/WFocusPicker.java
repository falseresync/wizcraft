package dev.falseresync.wizcraft.client.gui.hud.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.falseresync.wizcraft.client.gui.DrawingExt;
import dev.falseresync.wizcraft.client.gui.hud.WidgetController;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.FocusItem;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Deque;

/**
 * @implNote The focuses deque is validated to have at least one stack, therefore using {@link Deque#getFirst()} is okay
 */
public class WFocusPicker extends WWidget implements WControllerAware {
    protected static final int ITEM_OFFSET = 20;
    protected static final int ITEM_SIZE = 16;
    protected static final int HINT_OFFSET = 32;
    protected static final int HINT_SIZE = 16;
    protected static final int SELECTION_SIZE = 22;
    protected static final int LABEL_OFFSET = 4;
    protected static final Identifier SELECTION_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_selection.png");
    protected static final Identifier HINT_LEFT_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_hint_left.png");
    protected static final Identifier HINT_RIGHT_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_hint_right.png");
    protected final Deque<ItemStack> focuses;
    protected final WLabelWithSFX label;
    protected WidgetController<?, ?> controller = null;

    public WFocusPicker(Deque<ItemStack> focuses) {
        if (focuses.isEmpty()) {
            throw new IllegalArgumentException("Focus picker expects at least one focus, got none");
        }
        if (!focuses.stream().allMatch(focus -> focus.getItem() instanceof FocusItem)) {
            throw new IllegalArgumentException("Focus picker only accepts FocusItem stacks");
        }
        this.focuses = focuses;
        this.label = new WLabelWithSFX(getPicked().getName());
        this.label.enableShadow();
        this.label.enableFade();
        this.label.setHorizontalAlignment(HorizontalAlignment.CENTER);
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (this.controller == null || this.controller.getRemainingDisplayTicks() == 0) {
            return;
        }

        float opacity = Math.min(1, this.controller.getRemainingDisplayTicks() / 10f);

        RenderSystem.enableDepthTest();

        DrawingExt.square(context, calcX(x, SELECTION_SIZE), calcY(y, SELECTION_SIZE), SELECTION_SIZE, SELECTION_TEX, opacity);
        this.label.paint(context, x, y + getHeight() + LABEL_OFFSET, mouseX, mouseY);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, opacity);

        switch (this.focuses.size()) {
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
        var selected = this.focuses.peekFirst();
        context.drawItemWithoutEntity(selected, calcX(x, ITEM_SIZE) - ITEM_OFFSET, calcY(y, ITEM_SIZE));
    }

    protected void paint2(DrawContext context, int x, int y) {
        // Polling here allows to peek the next focus
        var selected = this.focuses.pollFirst();
        context.drawItemWithoutEntity(selected, calcX(x, ITEM_SIZE), calcY(y, ITEM_SIZE));

        var next = this.focuses.peekFirst();
        context.drawItemWithoutEntity(next, calcX(x, ITEM_SIZE) + ITEM_OFFSET, calcY(y, ITEM_SIZE));

        // Reset the deque
        this.focuses.offerFirst(selected);
    }

    protected void paint3(DrawContext context, int x, int y) {
        var last = this.focuses.peekLast();
        context.drawItemWithoutEntity(last, calcX(x, ITEM_SIZE) - ITEM_OFFSET, calcY(y, ITEM_SIZE));

        paint2(context, x, y);
    }

    protected void paintMany(DrawContext context, int x, int y) {
        DrawingExt.square(context, calcX(x, HINT_SIZE) - HINT_OFFSET, calcY(y, HINT_SIZE), HINT_SIZE, HINT_LEFT_TEX);
        DrawingExt.square(context, calcX(x, HINT_SIZE) + HINT_OFFSET, calcY(y, HINT_SIZE), HINT_SIZE, HINT_RIGHT_TEX);
        paint3(context, x, y);
    }

    public void pickNext() {
        this.focuses.offerLast(this.focuses.pollFirst());
        this.label.setText(getPicked().getName());
    }

    public ItemStack getPicked() {
        return this.focuses.peekFirst();
    }

    @Override
    public void addNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.focuses.getFirst().getName());
    }

    @Override
    public void setController(WidgetController<?, ?> controller) {
        this.controller = controller;
        this.label.setController(controller);
    }
}
