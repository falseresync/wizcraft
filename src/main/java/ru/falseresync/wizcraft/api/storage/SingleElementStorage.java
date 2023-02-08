package ru.falseresync.wizcraft.api.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtCompound;
import ru.falseresync.wizcraft.api.element.ElementConstants;
import ru.falseresync.wizcraft.common.names.WizCodecNames;
import ru.falseresync.wizcraft.common.names.WizNbtNames;

public abstract class SingleElementStorage extends SingleVariantStorage<ElementVariant> {
    private final Runnable onChange;

    protected SingleElementStorage(Runnable onChange) {
        this.onChange = onChange;
    }

    @Override
    protected ElementVariant getBlankVariant() {
        return ElementVariant.blank();
    }

    @Override
    protected void onFinalCommit() {
        onChange.run();
    }

    public void readNbt(NbtCompound nbt) {
        variant = ElementVariant.fromNbt(nbt.getCompound(WizNbtNames.VARIANT));
        amount = nbt.getLong(WizCodecNames.AMOUNT);
    }

    public static SingleElementStorage withFixedCapacity(long capacity, Runnable onChange) {
        return new SingleElementStorage(onChange) {
            @Override
            protected long getCapacity(ElementVariant variant) {
                return capacity;
            }
        };
    }

    public static SingleElementStorage withDefaultCapacity(Runnable onChange) {
        return withFixedCapacity(ElementConstants.JAR, onChange);
    }
}
