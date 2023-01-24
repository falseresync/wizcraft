package ru.falseresync.wizcraft.data;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import ru.falseresync.wizcraft.Wizcraft;
import ru.falseresync.wizcraft.api.data.WizJsonConstants;
import ru.falseresync.wizcraft.element.ElementalComposition;
import ru.falseresync.wizcraft.util.IdUtil;

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
        for (var entry : manager.findResources(WizJsonConstants.ELEMENTAL_COMPOSITIONS, id -> id.getPath().endsWith(WizJsonConstants.JSON_SUFFIX)).entrySet()) {
            try (var reader = entry.getValue().getReader()) {
                ElementalComposition.Manager.compositions.add(Wizcraft.GSON.fromJson(reader, ElementalComposition.class));
            } catch (Exception e) {
                Wizcraft.LOGGER.error("Error occurred while loading data json " + entry.getKey(), e);
            }
        }
    }
}
