package ru.falseresync.wizcraft.lib.autoregistry;

import com.google.common.reflect.TypeToken;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class Registrar<T> {
    private final Map<Identifier, T> identifiedObjects = new HashMap<>();
    private final Registry<T> registry;
    private final TypeToken<T> objectType;
    private final String objectSuffix;
    private final String modid;

    public Registrar(Registry<T> registry, TypeToken<T> objectType, String objectSuffix, String modid) {
        this.registry = registry;
        this.objectType = objectType;
        this.objectSuffix = objectSuffix;
        this.modid = modid;
    }

    /**
     * @return true if matches
     */
    @SuppressWarnings("unchecked")
    public boolean addIfMatches(String rawId, Object object) {
        var matches = objectType.isSupertypeOf(object.getClass());
        if (matches) {
            identifiedObjects.put(idWithoutSuffix(rawId), (T) object);
        }
        return matches;
    }

    public void run() {
        for (var entry : identifiedObjects.entrySet()) {
            Registry.register(registry, entry.getKey(), entry.getValue());
        }
    }

    private Identifier idWithoutSuffix(String fieldName) {
        return new Identifier(modid, StringUtils.removeEndIgnoreCase(StringUtils.removeEndIgnoreCase(fieldName.toLowerCase(), objectSuffix), "_"));
    }
}
