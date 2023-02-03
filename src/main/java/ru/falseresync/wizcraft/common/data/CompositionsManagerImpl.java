package ru.falseresync.wizcraft.common.data;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;
import ru.falseresync.wizcraft.api.element.Composition;
import ru.falseresync.wizcraft.api.element.CompositionsManager;
import ru.falseresync.wizcraft.common.Wizcraft;
import ru.falseresync.wizcraft.lib.IdUtil;
import ru.falseresync.wizcraft.lib.names.WizJsonNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CompositionsManagerImpl implements CompositionsManager, SimpleResourceReloadListener<List<Composition>> {
    private final List<Composition> compositions = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return IdUtil.wizId(WizJsonNames.COMPOSITIONS);
    }

    @Override
    public CompletableFuture<List<Composition>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var list = new ArrayList<Composition>();
            for (var resource : manager.findResources(WizJsonNames.COMPOSITIONS, id -> id.getPath().endsWith(WizJsonNames.JSON_SUFFIX)).entrySet()) {
                try (var reader = resource.getValue().getReader()) {
                    list.add(Wizcraft.GSON.fromJson(reader, Composition.class));
                } catch (Exception e) {
                    Wizcraft.LOGGER.error("Error occurred while loading data JSON " + resource.getKey(), e);
                }
            }
            return list;
        });
    }

    @Override
    public CompletableFuture<Void> apply(List<Composition> data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            compositions.clear();
            compositions.addAll(data);
        });
    }

    @Override
    public Optional<Composition> forItem(Item item) {
        for (var composition : compositions) {
            if (composition.ingredient().test(item.getDefaultStack())) {
                return Optional.of(composition);
            }
        }
        return Optional.empty();
    }
}
