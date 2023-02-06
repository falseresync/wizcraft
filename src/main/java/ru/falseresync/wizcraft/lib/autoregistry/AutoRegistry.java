package ru.falseresync.wizcraft.lib.autoregistry;

import com.google.common.reflect.TypeToken;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import ru.falseresync.wizcraft.lib.autoregistry.impl.AutoRegistryImpl;
import ru.falseresync.wizcraft.lib.autoregistry.impl.Registrar;

import java.util.Collection;

/**
 * <h2>USAGE EXAMPLE</h2>
 *
 * <p>In your ModInitializer:
 * <pre>{@code
 * // Don't try to override vanilla ones. You'll be a dick to everyone else using the lib
 * AutoRegistry.addRegistrar(MY_CUSTOM_REGISTRY, Custom.class, List.of("custom", "field_suffix"));
 * AutoRegistry.create(MODID, LOGGER)
 *      .addHolderClass(MyBlocks.class)
 *      .addHolderClass(MyItems.class)
 *      .run();
 * }</pre>
 *
 * <p>In your MyBlocks/Items/etc classes:
 * <pre>{@code
 * public class MyBlocks {
 *     public static final @RegistryObject MagicCauldronBlock MAGIC_CAULDRON = new MagicCauldronBlock();
 *     // Here the ID will be "super_duper". Useful if you keep multiple types of objects in the same class
 *     public static final @RegistryObject SuperDuperBlock SUPER_DUPER_BLOCK = new SuperDuperBlock();
 *     // Here the ID will be "mysterious_lamp". Useful if you want to keep the "_block" suffix for example
 *     public static final @RegistryObject(id="mysterious_lamp") MysteriousLanternBlock MYSTERIOUS_LANTERN = new MysteriousLanternBlock();
 *     // This works too, and just as above the ID will be "better_sword", without the "_item" suffix
 *     public static final @RegistryObject BetterSwordItem BETTER_SWORD_ITEM = new BetterSwordItem();
 * }
 * }</pre>
 *
 * @implNote help me pls
 */
public interface AutoRegistry {
    static AutoRegistry create(String modid, Logger logger) {
        return new AutoRegistryImpl(modid, logger);
    }

    /**
     * Use this for parametrized types
     *
     * <p>Example call:
     * <pre>{@code
     * AutoRegistry.addRegistrar(Registries.BLOCK, new TypeToken<Block>() {}, "block");
     * }</pre>
     *
     * <p><strong>Types in TypeToken&lt;here&gt; are NECESSARY.</strong>
     */
    static <T> void addRegistrar(Registry<T> registry, TypeToken<T> entryType, Collection<String> suffixes) {
        AutoRegistryImpl.addRegistrar(registry, entryType, suffixes);
    }

    static <T> void addRegistrar(Registry<T> registry, Class<T> entryType, Collection<String> suffixes) {
        addRegistrar(registry, TypeToken.of(entryType), suffixes);
    }

    AutoRegistry addHolderClass(Class<?> holderClass);

    /**
     * Registers all the objects in the provided class into the provided registry by the following rules:
     * <ul>
     *     <li>Only the public static fields marked with {@link RegistryObject} are scanned</li>
     *     <li>The ID is formed by lower-casing the field name and truncating its suffix,
     *     unless it's overridden through {@link RegistryObject#id()}</li>
     *     <li>Null fields are discarded with a warning message</li>
     *     <li>Fields with no {@link Registrar} are silently discarded</li>
     * </ul>
     */
    void run();
}
