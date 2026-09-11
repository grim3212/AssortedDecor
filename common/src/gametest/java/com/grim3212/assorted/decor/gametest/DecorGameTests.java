package com.grim3212.assorted.decor.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedDecor: the server-side state under the rendering (stored
 * blocks, painted colours, light, frame and wallpaper entities). The tests live in the
 * {@code *Tests} classes; this only lists them.
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
