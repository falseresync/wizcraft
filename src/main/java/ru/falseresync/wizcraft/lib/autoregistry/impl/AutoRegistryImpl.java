package ru.falseresync.wizcraft.lib.autoregistry.impl;

import com.google.common.reflect.TypeToken;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import ru.falseresync.wizcraft.lib.autoregistry.AutoRegistry;
import ru.falseresync.wizcraft.lib.autoregistry.RegistryObject;

import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
public class AutoRegistryImpl implements AutoRegistry {
    private static final List<Registrar<?>> REGISTRARS = new ArrayList<>();
    private static boolean hasAddedVanillaRegistrars = false;
    private final List<Registrar<?>.Executor> executors = new ArrayList<>();
    private final List<Class<?>> holderClasses = new ArrayList<>();
    private final String modid;
    private final Logger logger;

    public AutoRegistryImpl(String modid, Logger logger) {
        this.modid = modid;
        this.logger = logger;

        if (!hasAddedVanillaRegistrars) {
            addVanillaRegistrars();
            hasAddedVanillaRegistrars = true;
        }
    }

    public static <T> void addRegistrar(Registry<T> registry, TypeToken<T> objectType, Collection<String> suffixes) {
        REGISTRARS.add(new Registrar<>(registry, objectType, suffixes));
    }

    private static void addVanillaRegistrars() {
        var registriesClass = Registries.class;
        for (var field : registriesClass.getDeclaredFields()) {
            var modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                continue;
            }

            if (Registry.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType parameterizedType) {
                var typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length == 1) {
                    var suffixes = new ArrayList<String>();
                    var fieldName = StringUtils.removeEndIgnoreCase(field.getName().toLowerCase(), "_type");
                    suffixes.add(fieldName);
                    Collections.addAll(suffixes, fieldName.split("_"));

                    try {
                        addVanillaRegistrar(field.get(registriesClass), TypeToken.of(typeArguments[0]), suffixes);
                    } catch (IllegalAccessException e) {
                        throw new InaccessibleObjectException("Couldn't read a vanilla registry field: %s ".formatted(field.getName()));
                    }
                } else {
                    throw new IllegalArgumentException("Couldn't parse the type of a vanilla registry: %s".formatted(field.getName()));
                }
            }
        }
    }

    // oh my god
    private static <T> void addVanillaRegistrar(Object registry, TypeToken<T> objectType, Collection<String> suffixes) {
        addRegistrar((Registry<T>) registry, objectType, suffixes);
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
        REGISTRARS.forEach(registrar -> executors.add(registrar.createExecutor(modid)));

        for (var holderClass : holderClasses) {
            fields_iterator:
            for (var field : holderClass.getDeclaredFields()) {
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
                        logger.warn("Found a null @RegistryObject field, discarding it: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                        continue;
                    }

                    var rawId = annotations[0].id();
                    if (rawId.isBlank()) {
                        rawId = field.getName();
                    }

                    for (var executor : executors) {
                        if (executor.testThenAdd(rawId, registryObject)) {
                            continue fields_iterator;
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new InaccessibleObjectException("Couldn't read a @RegistryObject field: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                }
            }
        }
        executors.forEach(Registrar.Executor::run);
    }
}
