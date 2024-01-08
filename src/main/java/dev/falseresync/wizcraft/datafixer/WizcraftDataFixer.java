package dev.falseresync.wizcraft.datafixer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import dev.falseresync.wizcraft.datafixer.fixes.FlattenFocusStackNbtFix;
import dev.falseresync.wizcraft.datafixer.fixes.RenameSkyWandToWandItemNbtFix;
import dev.falseresync.wizcraft.datafixer.schema.WizSchema100;
import dev.falseresync.wizcraft.datafixer.schema.WizSchema200;
import dev.falseresync.wizcraft.datafixer.schema.WizSchema300;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockNameFix;
import net.minecraft.datafixer.fix.ChoiceTypesFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.fix.RenameBlockEntityFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

/**
 * @author Taken code and ideas from i509VCB's (never merged) PR to Fabric API
 */
public class WizcraftDataFixer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Wizcraft/DataFixer");
    public static final int DATA_VERSION = 300;
    private static final int VANILLA_SCHEMA_VERSION = DataFixUtils.makeKey(SharedConstants.getGameVersion().getSaveVersion().getId());
    private static final BiFunction<Integer, Schema, Schema> EMPTY_SCHEMA = IdentifierNormalizingSchema::new;
    private static final String KEY_DATA_VERSION = "wizcraft:data_version";
    public static final DataFixer FIXER = createFixer();

    private static Schema createInitialSchemaFromVanilla(int versionKey, Schema parent) {
        LOGGER.info("Started with a Vanilla Schema version of {}", VANILLA_SCHEMA_VERSION);
        return new Schema(0, Schemas.getFixer().getSchema(VANILLA_SCHEMA_VERSION));
    }

    private static DataFixer createFixer() {
        LOGGER.info("Building for a Wizcraft Schema version of {}", DATA_VERSION);
        var builder = new DataFixerBuilder(DATA_VERSION);

        builder.addSchema(0, WizcraftDataFixer::createInitialSchemaFromVanilla);

        var schema100 = builder.addSchema(100, WizSchema100::new);
        builder.addFixer(new ChoiceTypesFix(schema100, "Add Star projectile", TypeReferences.ENTITY));
        builder.addFixer(new ChoiceTypesFix(schema100, "Add Lensing pedestal and Energized worktable", TypeReferences.BLOCK_ENTITY));

        var schema200 = builder.addSchema(200, WizSchema200::new);
        builder.addFixer(RenameBlockEntityFix.create(schema200, "Rename Energized worktable to Plated worktable", id -> id.replace("energized", "plated")));
        builder.addFixer(BlockNameFix.create(schema200, "Rename Energized worktable to Plated worktable", id -> id.replace("energized", "plated")));
        builder.addFixer(ItemNameFix.create(schema200, "Rename Energized worktable to Plated worktable", id -> id.replace("energized", "plated")));

        var schema300 = builder.addSchema(300, WizSchema300::new);
        builder.addFixer(RenameBlockEntityFix.create(schema300, "Rename Plated worktable to Worktable", id -> id.replace("plated_", "")));
        builder.addFixer(BlockNameFix.create(schema300, "Rename Plated worktable to Worktable", id -> id.replace("plated_", "")));
        builder.addFixer(ItemNameFix.create(schema300, "Rename Plated worktable to Worktable", id -> id.replace("plated_", "")));
        builder.addFixer(ItemNameFix.create(schema300, "Rename Sky wand to Wand (ID)", id -> id.replace("sky_", "")));
        builder.addFixer(new RenameSkyWandToWandItemNbtFix(schema300));
        builder.addFixer(new FlattenFocusStackNbtFix(schema300));

        LOGGER.info("Bootstrapping an executor");
        return builder.buildOptimized(
                Set.of(),
                Executors.newSingleThreadExecutor(
                        new ThreadFactoryBuilder()
                                .setNameFormat("Wizcraft/DataFixer Thread %d")
                                .setDaemon(true)
                                .setPriority(1)
                                .build()));
    }

    public static int getDataVersion(NbtCompound nbt) {
        return nbt.contains(KEY_DATA_VERSION, NbtElement.INT_TYPE) ? nbt.getInt(KEY_DATA_VERSION) : 0;
    }

    public static void putDataVersion(NbtCompound nbt) {
        nbt.putInt(KEY_DATA_VERSION, DATA_VERSION);
    }
}
