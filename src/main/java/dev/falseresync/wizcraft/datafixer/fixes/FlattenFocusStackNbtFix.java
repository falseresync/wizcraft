package dev.falseresync.wizcraft.datafixer.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.ItemNbtFix;


/**
 * Must be applied after {@link net.minecraft.datafixer.fix.ItemNameFix} and {@link RenameSkyWandToWandItemNbtFix}
 */
public class FlattenFocusStackNbtFix extends ItemNbtFix {
    public FlattenFocusStackNbtFix(Schema outputSchema) {
        super(outputSchema, "Flatten FocusStack NBT", id -> id.equals("wizcraft:wand"));
    }

    @Override
    protected <T> Dynamic<T> fixNbt(Dynamic<T> dynamic) {
        return dynamic.get("wizcraft:wand").result()
                .flatMap(wandData -> wandData.get("focus_stack").get("stack").result()
                        .map(stackData -> wandData.set("focus_stack", stackData)))
                .map(wandData -> dynamic.set("wizcraft:wand", wandData))
                .orElse(dynamic);
    }
}
