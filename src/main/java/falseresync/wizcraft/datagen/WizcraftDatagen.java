package falseresync.wizcraft.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WizcraftDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(WizcraftModelProvider::new);
		pack.addProvider(WizcraftRecipeProvider::new);
		pack.addProvider(WizcraftBlockLootTableProvider::new);
		pack.addProvider(WizcraftBlockTagProvider::new);
		pack.addProvider(WizcraftItemTagProvider::new);
	}
}
