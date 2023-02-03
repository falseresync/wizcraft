package ru.falseresync.wizcraft.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.element.Composition;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.common.data.CompositionsManagerImpl;
import ru.falseresync.wizcraft.common.init.*;
import ru.falseresync.wizcraft.lib.CodecUtil;
import ru.falseresync.wizcraft.lib.registry.AutoRegistry;

public class Wizcraft implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wizcraft");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ElementAmount.class, CodecUtil.codecToDeserializer(ElementAmount.CODEC, Wizcraft.LOGGER))
            .registerTypeAdapter(Composition.class, CodecUtil.codecToDeserializer(Composition.CODEC, Wizcraft.LOGGER))
            .create();
    public static final String MODID = "wizcraft";
    public static WizcraftApiImpl API;

    @Override
    public void onInitialize() {
        AutoRegistry.create(MODID, LOGGER)
                .run(Registries.BLOCK, WizBlocks.class)
                .run(Registries.BLOCK_ENTITY_TYPE, WizBlockEntities.class)
                .run(Registries.ITEM, WizItems.class)
                .run(Registries.RECIPE_TYPE, WizRecipes.class)
                .run(Registries.RECIPE_SERIALIZER, WizRecipeSerializers.class)
                .run(WizRegistries.ELEMENT, WizElements.class);

        WizItemGroups.init();

        var compositionsManager = new CompositionsManagerImpl();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(compositionsManager);

        API = new WizcraftApiImpl(compositionsManager);
    }
}