package ru.falseresync.wizcraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.block.WizBlocks;
import ru.falseresync.wizcraft.block.entity.WizBlockEntities;
import ru.falseresync.wizcraft.element.ElementAmountDeserializer;
import ru.falseresync.wizcraft.element.ElementalComposition;
import ru.falseresync.wizcraft.data.WizDataReloadListener;
import ru.falseresync.wizcraft.element.WizElements;
import ru.falseresync.wizcraft.item.WizItemGroups;
import ru.falseresync.wizcraft.item.WizItems;
import ru.falseresync.wizcraft.recipe.WizRecipes;

public class Wizcraft implements ModInitializer {
	public static final Gson GSON;
	public static final Logger LOGGER;
	public static final String MODID = "wizcraft";

	static {
		GSON = new GsonBuilder()
				.registerTypeAdapter(ElementAmount.class, new ElementAmountDeserializer())
				.registerTypeAdapter(ElementalComposition.class, new ElementalComposition.Deserializer())
				.create();
		LOGGER = LoggerFactory.getLogger("wizcraft");
	}

	@Override
	public void onInitialize() {
		// Alphabetically (mostly):
		WizBlocks.register();
		WizBlockEntities.register();
		WizElements.register();
		WizItems.register();
		WizItemGroups.init();
		WizRecipes.register();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new WizDataReloadListener());
	}
}