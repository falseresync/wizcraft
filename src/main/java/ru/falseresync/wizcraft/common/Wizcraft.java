package ru.falseresync.wizcraft.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.element.Composition;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.common.data.CompositionsManagerImpl;
import ru.falseresync.wizcraft.common.init.*;
import ru.falseresync.wizcraft.lib.CodecUtil;
import ru.falseresync.wizcraft.lib.autoregistry.AutoRegistry;

import java.util.List;

public class Wizcraft implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wizcraft");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ElementAmount.class, CodecUtil.makeDeserializer(ElementAmount.CODEC, Wizcraft.LOGGER))
            .registerTypeAdapter(Composition.class, CodecUtil.makeDeserializer(Composition.CODEC, Wizcraft.LOGGER))
            .create();
    public static final String MODID = "wizcraft";
    public static WizcraftApiImpl API;

    @Override
    public void onInitialize() {
        AutoRegistry.addRegistrar(WizRegistries.ELEMENT, Element.class, List.of("element"));
        AutoRegistry.create(MODID, LOGGER)
                .addHolderClass(WizBlocks.class)
                .addHolderClass(WizBlockEntities.class)
                .addHolderClass(WizItems.class)
                .addHolderClass(WizElements.class)
                .run();

        WizItemGroups.init();

        var compositionsManager = new CompositionsManagerImpl();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(compositionsManager);

        API = new WizcraftApiImpl(compositionsManager);
    }
}