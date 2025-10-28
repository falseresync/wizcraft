package falseresync.lib.registry;

import falseresync.lib.logging.BetterLogger;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import org.slf4j.*;

import java.lang.reflect.*;

/**
 * <h2>Usage</h2>
 *
 * <p>In your ModInitializer:
 * <pre>{@code
 * new AutoRegistry("mod_id", LOGGER)
 *      .link(Registries.BLOCK, MyBlocks.class)
 *      .link(Registries.ITEM, MyItems.class);
 * }</pre>
 *
 * <p>In your MyBlocks/Items/etc classes:
 * <pre>{@code
 * public class MyBlocks {
 *     public static final @RegistryObject MagicCauldronBlock MAGIC_CAULDRON = new MagicCauldronBlock();
 * }
 * }</pre>
 */
public class AutoRegistry {
    private final String modId;
    private final BetterLogger logger;

    public AutoRegistry(String modId, BetterLogger logger) {
        this.modId = modId;
        this.logger = logger;
    }

    /**
     * Registers all the objects in the provided class into the provided registry by the following rules:
     * <ul>
     *     <li>Only the public static fields marked with @RegistryObject are scanned</li>
     *     <li>The ID is formed by lower-casing the field name</li>
     *     <li>Null fields are discarded with a warning message</li>
     * </ul>
     *
     * @param <T> type parameter must match the type of the fields
     * @implNote help
     */
    public <T> AutoRegistry link(Registry<T> registry, Class<?>... holderClasses) {
        int classesNoFound = holderClasses.length;
        int classesNoRegistered = 0;
        int fieldsNoFound = 0;
        int fieldsNoRegistered = 0;
        for (var holderClass : holderClasses) {
            var fields = holderClass.getDeclaredFields();
            var fieldsNoCurrentlyFound = fields.length;
            var fieldsNoCurrentlyRegistered = 0;
            for (var field : fields) {
                var modifiers = field.getModifiers();
                var annotations = field.getAnnotationsByType(RegistryObject.class);
                if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers) || annotations.length == 0) {
                    continue;
                }

                try {
                    var registryObject = field.get(holderClass);
                    if (registryObject == null) {
                        logger.warn("Found a null @RegistryObject field, discarding: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                        continue;
                    }

                    //noinspection unchecked
                    Registry.register(registry, Identifier.of(modId, field.getName().toLowerCase()), (T) registryObject);
                    fieldsNoCurrentlyRegistered += 1;
                } catch (IllegalAccessException e) {
                    throw new InaccessibleObjectException("Couldn't read a @RegistryObject field: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("A @RegistryObject field's type doesn't match the provided registry: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                }
            }

            if (fieldsNoCurrentlyFound > 0) {
                fieldsNoFound += fieldsNoCurrentlyFound;
                if (fieldsNoCurrentlyRegistered > 0) {
                    fieldsNoRegistered += fieldsNoCurrentlyRegistered;
                    classesNoRegistered += 1;
                } else {
                    logger.warn("Found a class with no valid @RegistryObjects: %s".formatted(holderClass.getCanonicalName()));
                }
            } else {
                logger.warn("Found a class without any fields: %s".formatted(holderClass.getCanonicalName()));
            }
        }
        if (classesNoRegistered == 0) {
            logger.warn("Nothing got registered into %s because there are no holder classes with valid @RegistryObject fields".formatted(registry.getKey()));
        }
        logger.debug("Registered %s valid @RegistryObjects into registry %s from %s holder classes with %s fields".formatted(fieldsNoRegistered, registry.getKey(), classesNoFound, fieldsNoFound));
        return this;
    }
}