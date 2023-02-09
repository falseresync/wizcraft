package ru.falseresync.wizcraft.common;

import com.google.common.primitives.Doubles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.falseresync.wizcraft.api.WizRegistries;
import ru.falseresync.wizcraft.api.WizcraftAddon;
import ru.falseresync.wizcraft.api.element.Composition;
import ru.falseresync.wizcraft.api.element.Element;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.common.data.CompositionsManagerImpl;
import ru.falseresync.wizcraft.common.init.*;
import ru.falseresync.wizcraft.lib.CodecUtil;
import ru.falseresync.wizcraft.lib.autoregistry.AutoRegistry;
import ru.falseresync.wizcraft.lib.worldevents.WorldEventUtil;

import java.util.List;

public class Wizcraft implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wizcraft");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ElementAmount.class, CodecUtil.toDeserializer(ElementAmount.CODEC, Wizcraft.LOGGER))
            .registerTypeAdapter(Composition.class, CodecUtil.toDeserializer(Composition.CODEC, Wizcraft.LOGGER))
            .create();
    public static final String MODID = "wizcraft";
    public static WizcraftApiImpl API;
    public static int MAGIC_CAULDRON_DISSOLVE;

    @Override
    public void onInitialize() {
        var addons = FabricLoader.getInstance().getEntrypoints("wizcraft-addon", WizcraftAddon.class);

        addons.forEach(WizcraftAddon::beforeWizcraft);

        AutoRegistry.addRegistrar(WizRegistries.ELEMENT, Element.class, List.of("element"));
        AutoRegistry.create(MODID, LOGGER)
                .addHolderClass(WizBlocks.class)
                .addHolderClass(WizBlockEntities.class)
                .addHolderClass(WizElements.class)
                .addHolderClass(WizItems.class)
                .addHolderClass(WizRecipes.class)
                .run();

        WizItemGroups.init();

        var compositionsManager = new CompositionsManagerImpl();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(compositionsManager);

        MAGIC_CAULDRON_DISSOLVE = WorldEventUtil.registerLocal((client, world, renderer, pos, data) -> {

            world.addParticle(
                    ParticleTypes.ASH,
                    pos.getX() + Doubles.constrainToRange(world.random.nextGaussian() / 2, -1, 1),
                    pos.getY() + 1,
                    pos.getZ() + Doubles.constrainToRange(world.random.nextGaussian() / 2, -1, 1),
                    Doubles.constrainToRange(world.random.nextGaussian() / 2, -1, 1),
                    Doubles.constrainToRange(world.random.nextGaussian(), -1, 1),
                    Doubles.constrainToRange(world.random.nextGaussian() / 2, -1, 1));

            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        });

        API = new WizcraftApiImpl(compositionsManager);

        addons.forEach(addon -> addon.afterWizcraft(API));
    }
}