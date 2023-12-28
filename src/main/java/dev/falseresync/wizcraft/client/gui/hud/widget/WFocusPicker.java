package dev.falseresync.wizcraft.client.gui.hud.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.FocusItem;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL14;

import java.util.Deque;

@SuppressWarnings("DataFlowIssue") // The focuses deque is validated to have at least one item
public class WFocusPicker extends WWidget implements WStateful {
    protected static final int OFFSET = 20;
    protected static final int HINT_OFFSET = 32;
    protected static final Identifier SELECTION_BOX_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_selection_box.png");
    protected static final Identifier HINT_LEFT_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_hint_left.png");
    protected static final Identifier HINT_RIGHT_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_hint_right.png");
    protected final Deque<ItemStack> focuses;
    protected final WLabelWithSFX pickedFocusName;
    protected int ticksToRemoval = 0;

    public WFocusPicker(Deque<ItemStack> focuses) {
        if (focuses.isEmpty()) {
            throw new IllegalArgumentException("Focus picker expects at least one focus, got none");
        }
        if (!focuses.stream().allMatch(focus -> focus.getItem() instanceof FocusItem)) {
            throw new IllegalArgumentException("Focus picker only accepts FocusItem stacks");
        }
        this.focuses = focuses;
        pickedFocusName = new WLabelWithSFX(getPicked().getName());
        pickedFocusName.enableShadow();
        pickedFocusName.enableFade();
        pickedFocusName.setHorizontalAlignment(HorizontalAlignment.CENTER);
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (ticksToRemoval == 0) {
            return;
        }
        var fade = Math.min(256, (int) (ticksToRemoval * 256F / 10F));

        RenderSystem.enableDepthTest();

        ScreenDrawing.texturedRect(context, x + getWidth() / 2 - 11, y + getHeight() / 2 - 11, 22, 22, SELECTION_BOX_TEX, Color.WHITE.toRgb() + fade << 24);
        pickedFocusName.paint(context, x, y + getHeight() + 4, mouseX, mouseY);

        // TODO: this doesn't work
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(255, 255, 255, fade);

        RenderSystem.enableBlend();
        switch (focuses.size()) {
            case 1 -> paint1(context, x, y, fade);
            case 2 -> paint2(context, x, y, fade);
            case 3 -> paint3(context, x, y, fade);
            default -> paintMany(context, x, y, fade);
        }
        RenderSystem.disableBlend();
    }

    protected void paint1(DrawContext context, int x, int y, int fade) {
        var selected = focuses.peekFirst();
        context.drawItemWithoutEntity(selected, x + getWidth() / 2 - 8 - OFFSET, y + getHeight() / 2 - 8);
    }

    protected void paint2(DrawContext context, int x, int y, int fade) {
        // Polling here allows to peek the next focus
        var selected = focuses.pollFirst();
        context.drawItemWithoutEntity(selected, x + getWidth() / 2 - 8, y + getHeight() / 2 - 8);

        var next = focuses.peekFirst();
        context.drawItemWithoutEntity(next, x + getWidth() / 2 - 8 + OFFSET, y + getHeight() / 2 - 8);

        // Reset the deque
        focuses.offerFirst(selected);
    }

    protected void paint3(DrawContext context, int x, int y, int fade) {
        var last = focuses.peekLast();
        context.drawItemWithoutEntity(last, x + getWidth() / 2 - 8 - OFFSET, y + getHeight() / 2 - 8);

        paint2(context, x, y, fade);
    }

    protected void paintMany(DrawContext context, int x, int y, int fade) {
        ScreenDrawing.texturedRect(context, x + getWidth() / 2 - 8 - HINT_OFFSET, y + getHeight() / 2 - 8, 16, 16, HINT_LEFT_TEX, Color.WHITE.toRgb());
        ScreenDrawing.texturedRect(context, x + getWidth() / 2 - 8 + HINT_OFFSET, y + getHeight() / 2 - 8, 16, 16, HINT_RIGHT_TEX, Color.WHITE.toRgb());
        paint3(context, x, y, fade);
    }

    public void pickNext() {
        focuses.offerLast(focuses.pollFirst());
        pickedFocusName.setText(getPicked().getName());
    }

    public ItemStack getPicked() {
        return focuses.peekFirst();
    }

    @Override
    public void addNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, focuses.peekFirst().getName());
    }

    @Override
    public void statefulTick(int ticksToRemoval) {
        this.ticksToRemoval = ticksToRemoval;
        pickedFocusName.statefulTick(ticksToRemoval);
    }
}
