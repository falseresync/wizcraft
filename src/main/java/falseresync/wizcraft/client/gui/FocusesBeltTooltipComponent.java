package falseresync.wizcraft.client.gui;

import falseresync.wizcraft.common.data.component.FocusesBeltComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class FocusesBeltTooltipComponent implements TooltipComponent {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/background");
    private static final int BOTTOM_MARGIN = 4;
    private static final int WIDTH_PER_COLUMN = 18;
    private static final int HEIGHT_PER_ROW = 20;
    private final FocusesBeltComponent focusesBelt;

    public FocusesBeltTooltipComponent(FocusesBeltComponent container) {
        this.focusesBelt = container;
    }

    @Override
    public int getHeight() {
        return getRowsHeight() + BOTTOM_MARGIN;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return getColumnsWidth();
    }

    private int getColumnsWidth() {
        return getColumns() * WIDTH_PER_COLUMN + 2;
    }

    private int getRowsHeight() {
        return getRows() * HEIGHT_PER_ROW + 2;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int columns = getColumns();
        int rows = getRows();
        context.drawGuiTexture(BACKGROUND_TEXTURE, x, y, this.getColumnsWidth(), this.getRowsHeight());

        int slotIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int slotX = x + column * 18 + 1;
                int slotY = y + row * 20 + 1;
                drawSlot(slotX, slotY, slotIndex++, context, textRenderer);
            }
        }
    }

    private void drawSlot(int x, int y, int index, DrawContext context, TextRenderer textRenderer) {
        if (index >= focusesBelt.size()) {
            drawSprite(context, x, y, SlotSprite.SLOT);
        } else {
            ItemStack itemStack = focusesBelt.getStack(index);
            drawSprite(context, x, y, SlotSprite.SLOT);
            context.drawItem(itemStack, x + 1, y + 1, index);
            context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
            if (index == 0) {
                HandledScreen.drawSlotHighlight(context, x + 1, y + 1, 0);
            }
        }
    }

    private void drawSprite(DrawContext context, int x, int y, SlotSprite sprite) {
        context.drawGuiTexture(sprite.texture, x, y, 0, sprite.width, sprite.height);
    }

    private int getColumns() {
        return Math.max(2, (int) Math.ceil(Math.sqrt((double) focusesBelt.size() + 1.0)));
    }

    private int getRows() {
        return (int) Math.ceil((double) focusesBelt.size() / (double) getColumns());
    }

    enum SlotSprite {
        SLOT(Identifier.ofVanilla("container/bundle/slot"), 18, 20);

        public final Identifier texture;
        public final int width;
        public final int height;

        SlotSprite(final Identifier texture, final int width, final int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }
    }
}
