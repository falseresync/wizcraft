package ru.falseresync.wizcraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.element.ElementalComposition;
import ru.falseresync.wizcraft.data.WizDataReloadListener;
import ru.falseresync.wizcraft.registry.*;

public class Wizcraft implements ModInitializer {
	public static final Gson GSON;
	public static final Logger LOGGER;
	public static final String MODID = "wizcraft";

	static {
		GSON = new GsonBuilder()
				.registerTypeAdapter(Element.class, new Element.Deserializer())
				.registerTypeAdapter(ElementalComposition.class, new ElementalComposition.Deserializer())
				.create();
		LOGGER = LoggerFactory.getLogger("wizcraft");
	}

	@Override
	public void onInitialize() {
		WizBlocks.register();
		WizItems.register();
		WizBlockEntities.register();
		WizElements.register();
		WizItemGroups.init();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new WizDataReloadListener());
	}
}