package dev.falseresync.wizcraft.datafixer.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.ItemNbtFix;


/**
 * Must be applied after ItemNameFix
 */
public class RenameSkyWandToWandItemNbtFix extends ItemNbtFix {
    public RenameSkyWandToWandItemNbtFix(Schema outputSchema) {
        super(outputSchema, "Rename Sky wand to Wand (NBT)", id -> id.equals("wizcraft:wand"));
    }

    @Override
    protected <T> Dynamic<T> fixNbt(Dynamic<T> dynamic) {
        return dynamic.get("wizcraft:sky_wand").result()
                .map(wandData -> dynamic.remove("wizcraft:sky_wand").set("wizcraft:wand", wandData))
                .orElse(dynamic);
    }
}
