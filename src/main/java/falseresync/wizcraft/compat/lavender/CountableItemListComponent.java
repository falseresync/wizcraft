package falseresync.wizcraft.compat.lavender;

import com.google.common.collect.ImmutableList;
import falseresync.wizcraft.common.recipe.CountableIngredient;
import io.wispforest.lavender.md.ItemListComponent;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;

/**
 * @see io.wispforest.lavender.md.ItemListComponent
 */
public class CountableItemListComponent extends ItemComponent {
    private float time = 0f;
    private @Nullable ImmutableList<ItemStack> items;
    private int currentStackIndex;

    protected CountableItemListComponent() {
        super(ItemStack.EMPTY);
        setTooltipFromStack(true);
        margins(Insets.bottom(4));
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);

        time += delta;
        if (time >= 20) {
            time -= 20;
            updateForItems();
        }
    }

    public CountableItemListComponent countableIngredient(CountableIngredient ingredient) {
        items = ImmutableList.copyOf(ingredient.getMatchingStacks().stream()
                .map(it -> it.copyWithCount(ingredient.count()))
                .toList());
        updateForItems();
        showOverlay(true);

        return this;
    }

    public CountableItemListComponent ingredient(Ingredient ingredient) {
        items = ImmutableList.copyOf(ingredient.getMatchingStacks());
        updateForItems();

        return this;
    }

    private void updateForItems() {
        if (items != null && !items.isEmpty()) {
            currentStackIndex = (currentStackIndex + 1) % items.size();
            stack(items.get(currentStackIndex));
        } else {
            currentStackIndex = 0;
            stack(ItemStack.EMPTY);
        }
    }

}
