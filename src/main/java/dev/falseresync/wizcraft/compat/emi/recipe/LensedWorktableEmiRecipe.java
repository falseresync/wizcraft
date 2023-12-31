package dev.falseresync.wizcraft.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.recipe.LensedWorktableRecipe;
import dev.falseresync.wizcraft.compat.emi.WizcraftEmiPlugin;
import net.minecraft.util.Identifier;

import java.util.List;

public class LensedWorktableEmiRecipe implements EmiRecipe {
    public static final EmiTexture SKY_WAND_TEX = new EmiTexture(
            new Identifier(Wizcraft.MODID, "textures/item/sky_wand.png"),
            0, 0, 16, 16, 16, 16, 16, 16);
    public static final EmiTexture ARROW_TEX = new EmiTexture(
            new Identifier(Wizcraft.MODID, "textures/gui/hud/skywand/focus_picker_hint_right.png"),
            0, 0, 16, 16, 16, 16, 16, 16);

    protected final Identifier id;
    protected final EmiStack result;
    protected final EmiIngredient worktableInput;
    protected final List<EmiIngredient> pedestalInputs;
    protected final List<EmiIngredient> allInputs;

    public LensedWorktableEmiRecipe(Identifier id, LensedWorktableRecipe recipe) {
        this.id = id;
        this.result = EmiStack.of(recipe.getResult());
        this.worktableInput = EmiIngredient.of(recipe.getWorktableInput());
        this.pedestalInputs = recipe.getPedestalInputs().stream().map(EmiIngredient::of).toList();
        this.allInputs = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
    }


    @Override
    public EmiRecipeCategory getCategory() {
        return WizcraftEmiPlugin.LENSED_WORKTABLE;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.allInputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.result);
    }

    @Override
    public int getDisplayWidth() {
        return 126;
    }

    @Override
    public int getDisplayHeight() {
        return 72;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.worktableInput, 27, 27);
        switch (this.pedestalInputs.size()) {
            case 1 -> {
                widgets.addSlot(this.pedestalInputs.get(0),27, 0);
                widgets.addSlot(54, 27);
                widgets.addSlot(27, 54);
                widgets.addSlot(0, 27);
            }
            case 2 -> {
                widgets.addSlot(this.pedestalInputs.get(0),27, 0);
                widgets.addSlot(54, 27);
                widgets.addSlot(this.pedestalInputs.get(1),27, 54);
                widgets.addSlot(0, 27);
            }
            case 3 -> {
                widgets.addSlot(this.pedestalInputs.get(0),27, 0);
                widgets.addSlot(this.pedestalInputs.get(1),54, 27);
                widgets.addSlot(this.pedestalInputs.get(2),27, 54);
                widgets.addSlot(27, 0);
            }
            case 4 -> {
                widgets.addSlot(this.pedestalInputs.get(0),27, 0);
                widgets.addSlot(this.pedestalInputs.get(1),54, 27);
                widgets.addSlot(this.pedestalInputs.get(2),27, 54);
                widgets.addSlot(this.pedestalInputs.get(3),0, 27);
            }
        }
        widgets.addTexture(SKY_WAND_TEX, 81, 9);
        widgets.addTexture(ARROW_TEX, 81, 27);
        widgets.addSlot(this.result, 108, 27);
    }
}
