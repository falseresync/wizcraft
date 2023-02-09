package ru.falseresync.wizcraft.lib.worldevents;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public abstract class WorldEventUtil {
    public static final int PREFIX = 10_000;
    private static int lastId = 0;
    private static final Int2ObjectMap<WorldEventProcessor> LOCAL_PROCESSORS = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<WorldEventProcessor> GLOBAL_PROCESSORS = new Int2ObjectOpenHashMap<>();

    public static int registerLocal(WorldEventProcessor processor) {
        lastId++;
        var trueId = PREFIX + lastId;
        LOCAL_PROCESSORS.put(trueId, processor);
        return trueId;
    }

    public static int registerGlobal(WorldEventProcessor processor) {
        lastId++;
        var trueId = PREFIX + lastId;
        GLOBAL_PROCESSORS.put(trueId, processor);
        return trueId;
    }

    public static WorldEventProcessor getGlobal(int id) {
        return GLOBAL_PROCESSORS.get(id);
    }

    public static WorldEventProcessor getLocal(int id) {
        return LOCAL_PROCESSORS.get(id);
    }
}
