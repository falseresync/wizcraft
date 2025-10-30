package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.item.focus.FocusPlating;
import net.minecraft.resources.ResourceLocation;

public class DatagenUtil {
    public static ResourceLocation suffixPlating(ResourceLocation id, FocusPlating plating) {
        return id.withSuffix("_plating_" + plating.name().toLowerCase());
    }
}
