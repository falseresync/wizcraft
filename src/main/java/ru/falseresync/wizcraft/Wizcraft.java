package ru.falseresync.wizcraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.api.element.ElementalComposition;
import ru.falseresync.wizcraft.data.ElementalCompositionsResourceReloadListener;
import ru.falseresync.wizcraft.registry.*;
import ru.falseresync.wizcraft.util.IdUtil;

import static ru.falseresync.wizcraft.util.IdUtil.id;

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
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ElementalCompositionsResourceReloadListener());
	}
}