package com.grim3212.assorted.decor.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedDecor.
 * <p>
 * The bodies live in common because the behaviour they check is common; each loader module only
 * registers them into {@code Registries.TEST_FUNCTION} through its own hook, and
 * {@code data/assorteddecor/test_instance/*.json} pairs each one with the shared {@code test_box}
 * structure.
 * <p>
 * This mod is mostly a rendering mod, and a headless gametest sees none of that. What these tests
 * cover is the server side state underneath it: the block a colorizer stores, the colour a paint
 * roller writes into a block state, the light a fireplace emits, the entities frames and wallpapers
 * really are. Anything that needs eyes is in {@code TESTING-CHECKLIST.md}.
 * <p>
 * The tests themselves are split by feature into the {@code *Tests} classes in this package,
 * with shared helpers in {@code DecorTestSupport}; this only lists them.
 */
public final class DecorGameTests {

    private DecorGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        ColorizerTests.register(out);
        PaintTests.register(out);
        LightTests.register(out);
        EntityDecorationTests.register(out);
        BlockDecorationTests.register(out);
        AssetTests.register(out);
    }
}
