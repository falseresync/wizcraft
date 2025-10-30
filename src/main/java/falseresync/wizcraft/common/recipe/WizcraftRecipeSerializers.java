package falseresync.wizcraft.common.recipe;

import falseresync.lib.registry.RegistryObject;

public class WizcraftRecipeSerializers {
    public static final @RegistryObject LensedWorktableRecipe.Serializer LENSED_WORKTABLE = new LensedWorktableRecipe.Serializer();
    public static final @RegistryObject CrucibleRecipe.Serializer CRUCIBLE = new CrucibleRecipe.Serializer();
}
