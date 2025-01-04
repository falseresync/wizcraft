package falseresync.wizcraft.common;

import falseresync.lib.registry.AutoRegistry;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import falseresync.wizcraft.common.entity.WizcraftEntities;
import falseresync.wizcraft.common.item.WizcraftItemGroups;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final String MOD_ID = "wizcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft");

    @Override
    public void onInitialize() {
        WizcraftItems.init();
        WizcraftSounds.init();
        new AutoRegistry(MOD_ID, LOGGER)
//                .link(Focus.REGISTRY, WizcraftFocuses.class)
                .link(Registries.ITEM_GROUP, WizcraftItemGroups.class)
                .link(Registries.DATA_COMPONENT_TYPE, WizcraftDataComponents.class)
                .link(Registries.ENTITY_TYPE, WizcraftEntities.class);
    }

    public static Identifier id(String id) {
        return Identifier.of(id);
    }

    public static Identifier wid(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static int findViewDistance(World world) {
        return world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().chunkLoadingManager.watchDistance;
    }
}
