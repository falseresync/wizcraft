package ru.falseresync.wizcraft.data;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.Wizcraft;
import ru.falseresync.wizcraft.api.element.ElementalComposition;
import ru.falseresync.wizcraft.util.IdUtil;

public class ElementalCompositionsResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return IdUtil.id("data");
    }

    @Override
    public void reload(ResourceManager manager) {
        for (var entry : manager.findResources("elemental_compositions", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().getReader()) {
                ElementalComposition.Manager.compositions.add(Wizcraft.GSON.fromJson(reader, ElementalComposition.class));
            } catch (Exception e) {
                Wizcraft.LOGGER.error("Error occurred while loading resource json " + entry.getKey(), e);
            }
        }
    }
}
