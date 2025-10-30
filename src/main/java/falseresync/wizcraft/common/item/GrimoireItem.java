package falseresync.wizcraft.common.item;

import io.wispforest.lavender.book.LavenderBookItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class GrimoireItem extends LavenderBookItem implements ActivatorItem {
    protected GrimoireItem(Properties settings) {
        super(settings, wid("grimoire"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var activationResult = activateBlock(ANY_BEHAVIORS, context);
        if (activationResult.consumesAction()) return activationResult;

        return super.useOn(context);
    }
}
