package dev.falseresync.wizcraft.common.lensedworktable;

import dev.falseresync.wizcraft.common.block.entity.PlatedWorktableBlockEntity;

public class CraftingWorktableMode {
    private final PlatedWorktableBlockEntity worktable;

    public CraftingWorktableMode(PlatedWorktableBlockEntity worktable) {
        this.worktable = worktable;
    }

    public boolean checkPreconditions() {
        return false;
    }
}
