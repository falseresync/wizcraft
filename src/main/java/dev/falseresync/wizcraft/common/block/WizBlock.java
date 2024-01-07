package dev.falseresync.wizcraft.common.block;

import dev.falseresync.wizcraft.api.HasId;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class WizBlock extends Block implements HasId {
    private final Identifier id;

    public WizBlock(Identifier id, Settings settings) {
        super(settings);
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return id;
    }
}
