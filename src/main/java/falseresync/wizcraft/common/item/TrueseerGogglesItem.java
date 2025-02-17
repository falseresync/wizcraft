package falseresync.wizcraft.common.item;

import dev.emi.trinkets.api.*;
import falseresync.wizcraft.common.data.attachment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class TrueseerGogglesItem extends TrinketItem {
    public TrueseerGogglesItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        if (entity instanceof PlayerEntity player) {
            player.setAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES, true);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        if (entity instanceof PlayerEntity player) {
            player.removeAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES);
        }
    }
}
