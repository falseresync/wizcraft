package dev.falseresync.common;

import dev.falseresync.common.item.SkyWandItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wizcraft implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wizcraft");

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

        var wand = new SkyWandItem(new FabricItemSettings().maxCount(1));
        Registry.register(Registries.ITEM, new Identifier("wizcraft:sky_wand"), wand);

        // TODO register lol
        FabricItemGroup.builder()
                .icon(() -> wand.getDefaultStack())
				.displayName(Text.translatable("itemGroup.wizcraft"))
                .entries((displayContext, entries) -> {
                    entries.add(wand);
                })
                .build();
    }
}