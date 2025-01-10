package falseresync.wizcraft.common.item;

import io.wispforest.lavender.book.LavenderBookItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class GrimoireItem extends LavenderBookItem implements ActivatorItem {
    protected GrimoireItem(Settings settings) {
        super(settings, wid("grimoire"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var activationResult = activateBlock(ANY_BEHAVIORS, context);
        if (activationResult.isAccepted()) return activationResult;

        return super.useOnBlock(context);
    }
}
