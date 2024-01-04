package dev.falseresync.wizcraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.ItemNbtFix;

public class SkyWandDataKeysRenameFix extends ItemNbtFix {
    public SkyWandDataKeysRenameFix(Schema outputSchema) {
        super(outputSchema, "Rename Sky wand Codec keys", id -> id.equals("wizcraft:sky_wand"));
    }

    @Override
    protected <T> Dynamic<T> fixNbt(Dynamic<T> dynamic) {
        var skyWandDataResult = dynamic.get("SkyWand").result();
        if (skyWandDataResult.isEmpty()) return dynamic;

        var skyWandDataDynamic = skyWandDataResult.get();

        var maxChargeResult = skyWandDataDynamic.get("MaxCharge").result();
        if (maxChargeResult.isPresent()) {
            skyWandDataDynamic = skyWandDataDynamic.set("max_charge", maxChargeResult.get());
        }

        var chargeResult = skyWandDataDynamic.get("charge").result();
        if (chargeResult.isPresent()) {
            skyWandDataDynamic = skyWandDataDynamic.set("charge", chargeResult.get());
        }

        var activeFocusResult = skyWandDataDynamic.get("ActiveFocus").result();
        if (activeFocusResult.isPresent()) {
            skyWandDataDynamic = skyWandDataDynamic.set("active_focus", activeFocusResult.get());
        }

        return dynamic.set("wizcraft:sky_wand", skyWandDataDynamic);
    }
}
