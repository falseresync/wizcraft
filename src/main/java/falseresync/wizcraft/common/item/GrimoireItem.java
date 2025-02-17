package falseresync.wizcraft.common.item;

import io.wispforest.lavender.book.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

import static falseresync.wizcraft.common.Wizcraft.*;

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
