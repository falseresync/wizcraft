package falseresync.wizcraft.common.data.component;

import com.mojang.serialization.DataResult;
import falseresync.lib.registry.RegistryObject;
import falseresync.wizcraft.common.item.FocusItem;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

public class WizcraftDataComponents {
    public static final @RegistryObject ComponentType<ItemStack> EQUIPPED_FOCUS_ITEM =
            ComponentType.<ItemStack>builder()
                    .codec(ItemStack.CODEC.validate(stack -> stack.getItem() instanceof FocusItem
                            ? DataResult.success(stack)
                            : DataResult.error(() -> "Only FocusItems are allowed")))
                    .packetCodec(ItemStack.PACKET_CODEC)
                    .build();

//    public static final @RegistryObject ComponentType<Focus> EQUIPPED_FOCUS =
//            ComponentType.<Focus>builder()
//                    .codec(Focus.REGISTRY.getCodec())
//                    .build();
}
