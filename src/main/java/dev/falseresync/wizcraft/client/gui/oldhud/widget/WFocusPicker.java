package dev.falseresync.wizcraft.client.gui.oldhud.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.falseresync.wizcraft.common.Wizcraft;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Deque;

@Environment(EnvType.CLIENT)
public class WFocusPicker extends WWidget {
    protected static final int OFFSET = 20;
    protected static final Identifier SELECTION_BOX_TEX = new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_selection_box.png");
    protected final Deque<ItemStack> focuses;

    public WFocusPicker(Data data) {
        focuses = data.focuses;
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        RenderSystem.enableDepthTest();

        context.drawTexture(SELECTION_BOX_TEX, x + getWidth() / 2 - 11, y + getHeight() / 2 - 11, 0, 0, 22, 22, 22, 22);

        switch (focuses.size()) {
            case 0 -> throw new IllegalStateException("No focuses to draw!");
            case 1 -> paint1(context, x, y);
            case 2 -> paint2(context, x, y);
            default -> paint3(context, x, y);
        }
    }

    protected void paint1(DrawContext context, int x, int y) {
        var selected = focuses.peekFirst();
        context.drawItemWithoutEntity(selected, x + getWidth() / 2 - 8 - OFFSET, y + getHeight() / 2 - 8);
    }

    protected void paint2(DrawContext context, int x, int y) {
        // Polling here allows to peek the next focus
        var selected = focuses.pollFirst();
        context.drawItemWithoutEntity(selected, x + getWidth() / 2 - 8, y + getHeight() / 2 - 8);

        var next = focuses.peekFirst();
        context.drawItemWithoutEntity(next, x + getWidth() / 2 - 8 + OFFSET, y + getHeight() / 2 - 8);

        // Reset the deque
        focuses.offerFirst(selected);
    }

    protected void paint3(DrawContext context, int x, int y) {
        var last = focuses.peekLast();
        context.drawItemWithoutEntity(last, x + getWidth() / 2 - 8 - OFFSET, y + getHeight() / 2 - 8);

        paint2(context, x, y);
    }

    public void pickNext() {
        focuses.offerLast(focuses.pollFirst());
    }

    public ItemStack getPicked() {
        return focuses.peekFirst();
    }

    public record Data(
            Deque<ItemStack> focuses
    ) {
        public Data {
            if (focuses.isEmpty()) {
                throw new IllegalArgumentException("Focus Picker expects a non-empty list of focuses, but has received an empty one");
            }
        }
    }
}
