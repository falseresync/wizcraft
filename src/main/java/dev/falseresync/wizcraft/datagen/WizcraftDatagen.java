package dev.falseresync.wizcraft.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WizcraftDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(WizModelProvider::new);
		pack.addProvider(WizRecipeProvider::new);
		pack.addProvider(WizBlockLootTableProvider::new);
		pack.addProvider(WizBlockTagProvider::new);
	}
}
