package dev.falseresync.wizcraft.datafixer;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class SchemaVersionComponent implements AutoSyncedComponent {
    public static final String KEY = "wizcraft:version";

    private int schemaVersion = 0;

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains(KEY, NbtElement.INT_TYPE)) {
            schemaVersion = tag.getInt(KEY);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(KEY, schemaVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaVersionComponent that = (SchemaVersionComponent) o;
        return schemaVersion == that.schemaVersion;
    }

    public int get() {
        return schemaVersion;
    }
}
