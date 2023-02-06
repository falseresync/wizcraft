package ru.falseresync.wizcraft.lib.autoregistry.impl;

import com.google.common.reflect.TypeToken;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public final class Registrar<T> {
    private final Registry<T> registry;
    private final TypeToken<T> entryType;
    private final Collection<String> suffixes;

    public Registrar(Registry<T> registry, TypeToken<T> entryType, Collection<String> suffixes) {
        this.registry = registry;
        this.entryType = entryType;
        this.suffixes = suffixes;
    }

    public Executor createExecutor(String modid) {
        return new Executor(modid);
    }

    public final class Executor {
        private final Map<Identifier, T> identifiedObjects = new HashMap<>();
        private final String modid;

        private Executor(String modid) {
            this.modid = modid;
        }

        /**
         * @return true if the object matches this Registrar's entry type
         */
        @SuppressWarnings("unchecked")
        public boolean testThenAdd(String rawId, Object object) {
            var matches = entryType.isSupertypeOf(object.getClass());
            if (matches) {
                identifiedObjects.put(makeIdWithoutSuffix(rawId), (T) object);
            }
            return matches;
        }

        public void run() {
            for (var entry : identifiedObjects.entrySet()) {
                Registry.register(registry, entry.getKey(), entry.getValue());
            }
        }

        private Identifier makeIdWithoutSuffix(String fieldName) {
            var nameWithoutSuffix = suffixes.stream().reduce(fieldName.toLowerCase(), (name, suffix) -> StringUtils.removeEndIgnoreCase(name, suffix));
            return new Identifier(modid, StringUtils.removeEndIgnoreCase(nameWithoutSuffix, "_"));
        }
    }
}
