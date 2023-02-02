package ru.falseresync.wizcraft.common.data;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.common.Wizcraft;
import ru.falseresync.wizcraft.lib.names.WizJsonNames;
import ru.falseresync.wizcraft.common.element.ElementalComposition;
import ru.falseresync.wizcraft.lib.IdUtil;

public class WizDataReloadListener implements SimpleSynchronousResourceReloadListener {
    private ResourceManager manager;

    @Override
    public Identifier getFabricId() {
        return IdUtil.id("data");
    }

    @Override
    public void reload(ResourceManager manager) {
        this.manager = manager;
        reloadElementalCompositions();
        this.manager = null;
    }

    private void reloadElementalCompositions() {
        for (var entry : manager.findResources(WizJsonNames.ELEMENTAL_COMPOSITIONS, id -> id.getPath().endsWith(WizJsonNames.JSON_SUFFIX)).entrySet()) {
            try (var reader = entry.getValue().getReader()) {
                ElementalComposition.Manager.COMPOSITIONS.add(Wizcraft.GSON.fromJson(reader, ElementalComposition.class));
            } catch (Exception e) {
                Wizcraft.LOGGER.error("Error occurred while loading data json " + entry.getKey(), e);
            }
        }
    }
}
