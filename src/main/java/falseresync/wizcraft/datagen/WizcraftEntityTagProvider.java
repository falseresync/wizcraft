package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.entity.WizcraftEntityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WizcraftEntityTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public WizcraftEntityTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(WizcraftEntityTags.PASSES_THROUGH_ENERGY_VEIL)
                .add(EntityType.ITEM)
                .add(EntityType.AREA_EFFECT_CLOUD)
                .add(EntityType.LIGHTNING_BOLT)
                .add(EntityType.MARKER)
                .add(EntityType.END_CRYSTAL)
                .add(EntityType.OMINOUS_ITEM_SPAWNER)
                .add(EntityType.EYE_OF_ENDER)
                .add(EntityType.ENDER_PEARL)
                .add(EntityType.EXPERIENCE_ORB)
                .add(EntityType.EXPERIENCE_BOTTLE)
                .forceAddTag(ConventionalEntityTypeTags.BOATS)
                .forceAddTag(ConventionalEntityTypeTags.MINECARTS);
    }
}
