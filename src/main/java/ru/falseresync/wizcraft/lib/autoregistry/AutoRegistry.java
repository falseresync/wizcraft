package ru.falseresync.wizcraft.lib.autoregistry;

import com.google.common.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;

import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>USAGE EXAMPLE</h2>
 *
 * <p>In your ModInitializer:
 * <pre>{@code
 * AutoRegistry.create(MODID, LOGGER)
 *      .addRegistrar(MY_CUSTOM_REGISTRY, Custom.class, "custom") // Don't try to override vanilla ones. Yours will be discarded
 *      .addHolderClass(MyBlocks.class)
 *      .addHolderClass(MyItems.class)
 *      .run();
 * }</pre>
 *
 * <p>In your MyBlocks/Items/etc classes:
 * <pre>{@code
 * public class MyBlocks {
 *     public static final @RegistryObject MagicCauldronBlock MAGIC_CAULDRON = new MagicCauldronBlock();
 *     // Here the ID will be "super_duper"
 *     public static final @RegistryObject SuperDuperBlock SUPER_DUPER_BLOCK = new SuperDuperBlock();
 *     // Here the ID will be "mysterious_lamp". Useful if you keep multiple types of objects in the same class
 *     public static final @RegistryObject(id="mysterious_lamp") MysteriousLanternBlock MYSTERIOUS_LANTERN = new MysteriousLanternBlock();
 *     // This works too
 *     public static final @RegistryObject BetterSwordItem BETTER_SWORD_ITEM = new BetterSwordItem();
 * }
 * }</pre>
 *
 * @implNote help me pls
 */
public class AutoRegistry {
    private final List<Registrar<?>> registrars = new ArrayList<>();
    private final List<Class<?>> holderClasses = new ArrayList<>();
    private final String modid;
    private final Logger logger;

    @SuppressWarnings("Convert2Diamond")
    protected AutoRegistry(String modid, Logger logger) {
        this.modid = modid;
        this.logger = logger;
        addRegistrar(Registries.BLOCK, Block.class, "block");
        addRegistrar(Registries.BLOCK_ENTITY_TYPE, new TypeToken<BlockEntityType<?>>() {}, "block_entity");
        addRegistrar(Registries.ITEM, Item.class, "item");
    }

    public static AutoRegistry create(String modid, Logger logger) {
        return new AutoRegistry(modid, logger);
    }

    /**
     * <p>Example call:
     * <pre>{@code
     * addRegistrar(Registries.BLOCK, new TypeToken<Block>() {}, "block");
     * }</pre>
     *
     * <p><strong>Types in TypeToken&lt;here&gt; are NECESSARY.</strong>
     */
    public <T> AutoRegistry addRegistrar(Registry<T> registry, TypeToken<T> objectType, String objectSuffix) {
        registrars.add(new Registrar<>(registry, objectType, objectSuffix, modid));
        return this;
    }

    public <T> AutoRegistry addRegistrar(Registry<T> registry, Class<T> objectType, String objectSuffix) {
        return addRegistrar(registry, TypeToken.of(objectType), objectSuffix);
    }

    public AutoRegistry addHolderClass(Class<?> holderClass) {
        holderClasses.add(holderClass);
        return this;
    }

    /**
     * Registers all the objects in the provided class into the provided registry by the following rules:
     * <ul>
     *     <li>Only the public static fields marked with {@link RegistryObject} are scanned</li>
     *     <li>The ID is formed by lower-casing the field name and truncating its suffix,
     *     unless it's overridden through {@link RegistryObject#id()}</li>
     *     <li>Null fields are discarded with a warning message</li>
     *     <li>Fields with no {@link Registrar} are silently discarded</li>
     * </ul>
     */
    public void run() {
        for (var holderClass : holderClasses) {
            fields_iterator: for (var field : holderClass.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                var annotations = field.getAnnotationsByType(RegistryObject.class);
                if (!Modifier.isStatic(modifiers)
                        || !Modifier.isPublic(modifiers)
                        || annotations.length == 0) {
                    continue;
                }

                try {
                    var registryObject = field.get(holderClass);
                    if (registryObject == null) {
                        logger.warn("Found a null @RegistryObject field, discarding: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                        continue;
                    }

                    var rawId = annotations[0].id();
                    if (rawId.isBlank()) {
                        rawId = field.getName();
                    }
                    for (var registrar : registrars) {
                        if (registrar.addIfMatches(rawId, registryObject)) {
                            continue fields_iterator;
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new InaccessibleObjectException("Couldn't read a @RegistryObject field: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                }
            }
        }
        for (var registrar : registrars) {
            registrar.run();
        }
    }
}
